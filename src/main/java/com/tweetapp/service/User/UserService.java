package com.tweetapp.service.User;

import com.tweetapp.exceptionHandler.RecordNotFoundException;
import com.tweetapp.exceptionHandler.UserException;
import com.tweetapp.model.Jwt.JwtResponse;
import com.tweetapp.model.ResponseDTO;
import com.tweetapp.model.User;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface UserService {
    ResponseEntity<ResponseDTO<List<Document>>> getAllUsers() throws RecordNotFoundException;

    ResponseEntity<ResponseDTO<User>> getUserDetailByUsername(String username) throws RecordNotFoundException;

    ResponseEntity<ResponseDTO<String>> register(String user, MultipartFile profilePicture) throws UserException, IOException;

    ResponseEntity<ResponseDTO<JwtResponse>> login(String username, String password) throws Exception;

    ResponseEntity<ResponseDTO<String>> forgot(String username, String newPassword) throws RecordNotFoundException;
}
