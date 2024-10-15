package com.tweetapp.service.Tweet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tweetapp.exceptionHandler.DeleteException;
import com.tweetapp.exceptionHandler.RecordNotFoundException;
import com.tweetapp.exceptionHandler.TweetException;
import com.tweetapp.model.ResponseDTO;
import com.tweetapp.model.Tweet;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

public interface TweetService {
    ResponseEntity<ResponseDTO<String>> postNewTweet(String username, String tweetDetail, MultipartFile image) throws IOException, TweetException;

    ResponseEntity<ResponseDTO<List<Document>>> getAllTweets();

    ResponseEntity<ResponseDTO<Tweet>> updateTweetById(String username, String tweetDetail, MultipartFile image, String id) throws TweetException, IOException, RecordNotFoundException;

    ResponseEntity<ResponseDTO<String>> deleteTweetById(String username, String id) throws DeleteException, RecordNotFoundException;

    ResponseEntity<ResponseDTO<Tweet>> updateLikesOfTweetByID(String username, String id, Boolean like) throws RecordNotFoundException;

    ResponseEntity<ResponseDTO<Tweet>> replyToTweetByID(String username, String tweetDetail, MultipartFile image, String id) throws IOException, TweetException, RecordNotFoundException;

    ResponseEntity<ResponseDTO<List<Tweet>>> getAllTweetsByUsername(String username) throws RecordNotFoundException;
}
