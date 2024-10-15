package com.tweetapp.service.Tweet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.exceptionHandler.DeleteException;
import com.tweetapp.exceptionHandler.RecordNotFoundException;
import com.tweetapp.exceptionHandler.TweetException;
import com.tweetapp.model.ResponseDTO;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repository.DatabaseSequenceRepo;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;
import com.tweetapp.service.SequenceGeneratorSequence;
import com.tweetapp.service.Tweet.TweetService;
import com.tweetapp.utility.Utility;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinarySubType;
import org.bson.Document;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TweetServiceImpl implements TweetService {

    @Autowired
    private TweetRepository tweetRepository;


    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoOperations mongoOperations;


    @Autowired
    private DatabaseSequenceRepo databaseSequenceRepo;
    @Autowired
    private SequenceGeneratorSequence sequenceGeneratorSequence;

    @Autowired
    private ObjectMapper objm;

    @Autowired
    private Utility utility;


    public void constraintChecker(Tweet tweet) throws TweetException {
        String tagDetail = tweet.getTagDetail();
        String tweetMessage = tweet.getTweetMessage();
        if(tweetMessage.isEmpty() || tweetMessage.length()>144){
            throw new TweetException("Please enter the tweet message in the range 1 to 144 characters");
        }

        if(tagDetail.length()>50){
            throw new TweetException("Please enter the tag detail below or equal to 50 characters");
        }
    }


    public Tweet setRemainingValues(Tweet tweet,MultipartFile image,String username) throws IOException {


        tweet.setTweetPhotoFileName(image.getOriginalFilename());
        tweet.setTweetPhoto(new Binary(BsonBinarySubType.BINARY,image.getBytes()));

        tweet.setTweetPublishedDate(utility.getCurrentDateWithTime());
        tweet.setUserID(username);


        return tweet;

    }




    @Override
    public ResponseEntity<ResponseDTO<String>> postNewTweet(String username, String tweetDetail, MultipartFile image) throws IOException, TweetException {


        Tweet tweet = objm.readValue(tweetDetail,Tweet.class);



        log.info("Inside the service post new tweet method" +
                " New Tweet: " + tweet + " Username: " + username);


        constraintChecker(tweet);

        //User user = userRepository.findByUserName(username);



        tweet = setRemainingValues(tweet,image,username);
        tweet.setTweetId(sequenceGeneratorSequence.generateSequence(Tweet.SEQUENCE_NAME));

        tweetRepository.save(tweet);


        Query query = new Query();

        query.addCriteria(Criteria.where("loginId").is(username));

        User existingUser = mongoOperations.findOne(query,User.class);

        List<Tweet> userTweetList = existingUser.getAllTweets();

        if(userTweetList==null){
            userTweetList = new ArrayList<>();
            userTweetList.add(tweet);
        }else{
            userTweetList.add(tweet);
        }

        existingUser.setAllTweets(userTweetList);
        mongoOperations.save(existingUser);



        return ResponseEntity.ok(new ResponseDTO<String>(HttpStatus.OK.value(), HttpStatus.OK,"Post Successfully"));
    }

    @Override
    public ResponseEntity<ResponseDTO<List<Document>>> getAllTweets() {

        SortOperation sortOperation =
                Aggregation.sort(Sort.Direction.DESC,"tweetPublishedDate");


        Aggregation aggregation =
                Aggregation.newAggregation(sortOperation);

        List<Document> tweetList =
                mongoTemplate.aggregate(aggregation, Tweet.class,Document.class).getMappedResults();




        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK, tweetList));
    }

    @Override
    public ResponseEntity<ResponseDTO<Tweet>> updateTweetById(String username, String tweetDetail, MultipartFile image, String id) throws TweetException, IOException, RecordNotFoundException {


        Tweet newTweet = objm.readValue(tweetDetail,Tweet.class);




        log.info("Inside the service post new tweet method" +
                " New Tweet: " + newTweet + " Username: " + username);


        constraintChecker(newTweet);

       // User user = userRepository.findByUserName(username);

        newTweet = setRemainingValues(newTweet,image,username);



        Query query = new Query();

        List<Criteria> criteria = new ArrayList<>();


        criteria.add(Criteria.where("userID").is(username));
        criteria.add(Criteria.where("tweetId").is(Long.valueOf(id)));

        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));

        Tweet oldTweet = mongoOperations.findOne(query,Tweet.class);

        if(oldTweet==null){
            throw new RecordNotFoundException("Tweet not found");
        }else{
            if(!oldTweet.getTagDetail().equals(newTweet.getTagDetail()) && newTweet.getTagDetail().length()!=0){
                oldTweet.setTagDetail(newTweet.getTagDetail());
            }
            if(!oldTweet.getTweetMessage().equals(newTweet.getTweetMessage())){
                oldTweet.setTweetMessage(newTweet.getTweetMessage());
            }
            if(newTweet.getTweetPhotoFileName().length()!=0){
                oldTweet.setTweetPhotoFileName(newTweet.getTweetPhotoFileName());
                oldTweet.setTweetPhoto(newTweet.getTweetPhoto());
            }
        }



        mongoOperations.save(oldTweet);

        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK, oldTweet));
    }

    @Override
    public ResponseEntity<ResponseDTO<String>> deleteTweetById(String username, String id) throws DeleteException, RecordNotFoundException {
        Query query = new Query();

        List<Criteria> criteria = new ArrayList<>();


        //User user =  userRepository.findByUserName(username);

        criteria.add(Criteria.where("userID").is(username));
        criteria.add(Criteria.where("tweetId").is(Long.valueOf(id)));

        query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[criteria.size()])));

        Tweet deletedTweet = mongoOperations.findAndRemove(query,Tweet.class);

        if(deletedTweet==null){
            throw new RecordNotFoundException("Tweet with this ID: " + id + " doesn't exist");
        }

        //
        //
        //
        Query query2 = new Query();

        List<Criteria> criteria2 = new ArrayList<>();


        criteria2.add(Criteria.where("userID").is(deletedTweet.getUserID()));
        criteria2.add(Criteria.where("tweetId").is(Long.valueOf(deletedTweet.getTweetId())));

        query2.addCriteria(new Criteria().andOperator(criteria2.toArray(new Criteria[criteria2.size()])));


        Tweet exist = mongoOperations.findOne(query2,Tweet.class);

        if(exist!=null){
            throw new RecordNotFoundException("Not Deleted Successfully");
        }else{

            return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK,"Deleted Successfully!!!"));
        }

    }

    @Override
    public ResponseEntity<ResponseDTO<Tweet>> updateLikesOfTweetByID(String username, String id, Boolean like) throws RecordNotFoundException {

        Query userQuery = new Query();


        userQuery.addCriteria(Criteria.where("loginId").is(username));

        User user = mongoOperations.findOne(userQuery,User.class);

        if(user==null){
            throw new RecordNotFoundException("Username don't exist please create the account first!!!");
        }

        Query tweetQuery = new Query();


        tweetQuery.addCriteria(Criteria.where("tweetId").is(Long.valueOf(id)));

        Tweet existingTweet = mongoOperations.findOne(tweetQuery,Tweet.class);

        if(existingTweet==null){
            throw new RecordNotFoundException("Tweet Not Found");
        }


        long totalLikes = existingTweet.getUsersLikeTweet();
        if(like){
            totalLikes += 1;
            existingTweet.setUsersLikeTweet(totalLikes);
        }

        mongoOperations.save(existingTweet);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK, existingTweet));
    }

    @Override
    public ResponseEntity<ResponseDTO<Tweet>> replyToTweetByID(String username, String tweetDetail, MultipartFile image, String id) throws IOException, TweetException, RecordNotFoundException {
        Query query = new Query();

        query.addCriteria(Criteria.where("tweetId").is(Long.valueOf(id)));

        Tweet existingTweet = mongoOperations.findOne(query,Tweet.class);


        if(existingTweet==null){
            throw new RecordNotFoundException("Tweet with this ID: " + id + " doesn't exist");
        }

        Tweet replyTweet = objm.readValue(tweetDetail,Tweet.class);



        constraintChecker(replyTweet);

        //User user = userRepository.findByUserName(username);

        replyTweet = setRemainingValues(replyTweet,image,username);
        replyTweet.setTweetId(sequenceGeneratorSequence.generateSequence(Tweet.SEQUENCE_NAME));

        tweetRepository.save(replyTweet);

        List<Tweet> replyList = existingTweet.getUsersReplyOnTweet();


        if(replyList==null){
            replyList = new ArrayList<>();
        }

        replyList.add(replyTweet);

        existingTweet.setUsersReplyOnTweet(replyList);

        mongoOperations.save(existingTweet);
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK,existingTweet));
    }

    @Override
    public ResponseEntity<ResponseDTO<List<Tweet>>> getAllTweetsByUsername(String username) throws RecordNotFoundException {

        List<Tweet> tweetListByUserName = tweetRepository.findByUserName(username);
        log.info("Inside the Tweet By Username Method"  + tweetListByUserName.toString());

        if(tweetListByUserName.isEmpty()){
            throw new RecordNotFoundException("Tweet Not Found For Particular Username: " + username);
        }


        Query query = new Query();
        query.addCriteria(Criteria.where("loginId").is(username));

        User user = mongoOperations.findOne(query,User.class);







        user.setAllTweets(tweetListByUserName);
        mongoOperations.save(user);






        return ResponseEntity.ok(new ResponseDTO<List<Tweet>>(HttpStatus.OK.value(), HttpStatus.OK,tweetListByUserName));
    }


}
