package com.tweetapp.service.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweetapp.exceptionHandler.RecordNotFoundException;
import com.tweetapp.exceptionHandler.UserException;
import com.tweetapp.model.Jwt.JwtResponse;
import com.tweetapp.model.ResponseDTO;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repository.UserRepository;
import com.tweetapp.service.AuthenticationService;
import com.tweetapp.utility.JWTUtility;
import com.tweetapp.utility.Utility;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;




    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private ObjectMapper objm;

    @Autowired
    private Utility utility;

    @Autowired
    private JWTUtility jwtUtility;


    public User setRemainingValues(User user, MultipartFile image) throws IOException {


        user.setUserProfilePictureFileName(image.getOriginalFilename());
        user.setProfilePicture(new Binary(BsonBinarySubType.BINARY,image.getBytes()));


        user.setAccountCreationDate(utility.getCurrentDateWithTime());


        return user;

    }

    @Override
    public ResponseEntity<ResponseDTO<List<Document>>> getAllUsers() throws RecordNotFoundException {

        SortOperation sortOperation =
                Aggregation.sort(Sort.Direction.ASC,"loginId");



        Aggregation aggregation =
                Aggregation.newAggregation(sortOperation);

        List<Document> usersList =
                mongoTemplate.aggregate(aggregation, User.class,Document.class).getMappedResults();

        if(usersList.isEmpty()){
            throw new RecordNotFoundException("No users are currently registered in this app");
        }


        return ResponseEntity.ok(new ResponseDTO<List<Document>>(HttpStatus.OK.value(), HttpStatus.OK, usersList));
    }

    @Override
    public ResponseEntity<ResponseDTO<User>> getUserDetailByUsername(String username) throws RecordNotFoundException {

        User user = userRepository.findByUserName(username);

        if (user==null){
            throw new RecordNotFoundException("User Not Found");
        }

        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK,user));
    }

    @Override
    public ResponseEntity<ResponseDTO<String>> register(String userDetail, MultipartFile profilePicture) throws UserException, IOException {


        User user = objm.readValue(userDetail,User.class);

        User existingUserByUserName =  userRepository.findByUserName(user.getLoginId());

        if(existingUserByUserName!=null){
            throw new UserException("User with this user name already exist");
        }

        User existingUserByUserEmail =  userRepository.findByUserEmail(user.getEmail());

        if(existingUserByUserEmail!=null){
            throw new UserException("User with this email already exist");
        }


        user = setRemainingValues(user,profilePicture);



        user.setAllTweets(null);

        userRepository.save(user);



        //Optional<User> savedUser =  userRepository.save(user)
        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK, "Account Created Successfully!!"));
    }

    @Override
    public ResponseEntity<ResponseDTO<JwtResponse>> login(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            username,
                            password
                    )
            );
        }catch(BadCredentialsException e){
            throw new Exception("Invalid_credentials", e);
        }
        final UserDetails userDetails = authenticationService.loadUserByUsername(username);

        final String token = jwtUtility.generateToken(userDetails);

        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK, new JwtResponse(token)));

    }

    @Override
    public ResponseEntity<ResponseDTO<String>> forgot(String username, String newPassword) throws RecordNotFoundException {

        Query query = new Query();

        query.addCriteria(Criteria.where("loginId").is(username));

        User existingUser = mongoOperations.findOne(query,User.class);


        if(existingUser==null){
            throw new RecordNotFoundException("Username Not Exist");
        }


        existingUser.setPassword(newPassword);
        existingUser.setConfirmPassword(newPassword);

        mongoOperations.save(existingUser);

        return ResponseEntity.ok(new ResponseDTO<>(HttpStatus.OK.value(), HttpStatus.OK,"Password Change Successfully"));
    }


}
