package com.tweetapp.controller;

import com.tweetapp.exceptionHandler.RecordNotFoundException;
import com.tweetapp.exceptionHandler.UserException;
import com.tweetapp.model.Jwt.JwtResponse;
import com.tweetapp.model.ResponseDTO;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.service.User.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.models.Response;
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
public class UserController {

    @Autowired
    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<String>> register(@RequestParam("user") @Valid String user,@RequestParam(value = "image",required = false) MultipartFile image) throws UserException, IOException {
        return userService.register(user,image);
    }


    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/users/all")
    public ResponseEntity<ResponseDTO<List<Document>>> getAllUsers() throws RecordNotFoundException {
        return userService.getAllUsers();
    }

    @ApiOperation(value = "", authorizations = { @Authorization(value="jwtToken") })
    @GetMapping("/user/search/{username}")
    public ResponseEntity<ResponseDTO<User>> getUserDetailByUsername(@PathVariable("username") String username) throws RecordNotFoundException {
        return userService.getUserDetailByUsername(username);
    }


   // public List<Tweet>

    @GetMapping("/login")
    public ResponseEntity<ResponseDTO<JwtResponse>> login(@RequestParam("username") String username, @RequestParam("password")String password) throws Exception {
        return userService.login(username,password);
    }


    @GetMapping("/{username}/forgot")
    public ResponseEntity<ResponseDTO<String>> forgotPassword(@PathVariable("username") String username,@RequestParam("newPassword") String newPassword) throws RecordNotFoundException {
        return userService.forgot(username,newPassword);
    }










}
