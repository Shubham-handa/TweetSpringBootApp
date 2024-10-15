package com.tweetapp.controller;

import com.tweetapp.exceptionHandler.DeleteException;
import com.tweetapp.exceptionHandler.RecordNotFoundException;
import com.tweetapp.exceptionHandler.TweetException;
import com.tweetapp.model.ResponseDTO;
import com.tweetapp.model.Tweet;
import com.tweetapp.service.Tweet.TweetService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("${base.url}")
@Slf4j
public class TweetController {


    @Autowired
    private TweetService tweetService;

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/{username}/add")
    public ResponseEntity<ResponseDTO<String>> postNewTweet(@PathVariable("username") String username, @RequestParam("tweet") @Valid String tweetDetail, @RequestParam(value = "image",required = false)MultipartFile image) throws IOException, TweetException {
        log.info("Inside the post new tweet method " + username);

        return tweetService.postNewTweet(username,tweetDetail,image);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<Document>>> getAllTweets(){
        return tweetService.getAllTweets();
    }



    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/{username}")
    public ResponseEntity<ResponseDTO<List<Tweet>>> getAllTweetsByUsername(@PathVariable("username") String username) throws RecordNotFoundException {
        return tweetService.getAllTweetsByUsername(username);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PutMapping("/{username}/update/{id}")
    public ResponseEntity<ResponseDTO<Tweet>> updateTweetById(@PathVariable("username") String username, @RequestParam("tweet") @Valid String tweetDetail, @RequestParam(value = "image",required = false)MultipartFile image, @PathVariable("id") String id) throws RecordNotFoundException, TweetException, IOException {
        return tweetService.updateTweetById(username,tweetDetail,image,id);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PostMapping("/{username}/reply/{id}")
    public ResponseEntity<ResponseDTO<Tweet>> replyToTweetByID(@PathVariable("username") String username, @RequestParam("tweet") @Valid String tweetDetail, @RequestParam(value = "image",required = false)MultipartFile image, @PathVariable("id") String id) throws IOException, TweetException, RecordNotFoundException {
        return tweetService.replyToTweetByID(username,tweetDetail,image,id);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @PutMapping("/{username}/like/{id}")
    public ResponseEntity<ResponseDTO<Tweet>> updateLikesOfTweetByID(@PathVariable("username") String username,@PathVariable("id") String id, @RequestParam("like") Boolean like) throws RecordNotFoundException {

        return tweetService.updateLikesOfTweetByID(username,id,like);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @DeleteMapping("/{username}/delete/{id}")
    public ResponseEntity<ResponseDTO<String>> deleteTweetById(@PathVariable("username") String username,@PathVariable("id") String id) throws DeleteException, RecordNotFoundException {
        return tweetService.deleteTweetById(username,id);
    }






}
