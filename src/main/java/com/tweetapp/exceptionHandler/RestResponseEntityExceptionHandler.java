package com.tweetapp.exceptionHandler;

import com.tweetapp.model.ErrorMessage;
import com.tweetapp.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@ResponseStatus
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errorMsg = new ArrayList<>();

        ex.getFieldErrors()
                .forEach(err -> errorMsg.add(err.getDefaultMessage()));

        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST,
                errorMsg.get(0)
        );

        log.info("Inside the rest response entity exception error message" + message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


    @ExceptionHandler(TweetException.class)
    public ResponseEntity<ErrorMessage> customException(TweetException tweetException, WebRequest request){
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST,
                tweetException.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }


//    @ExceptionHandler(DeleteException.class)
//    public ResponseEntity<ErrorMessage> deleteException(DeleteException deleteException, WebRequest request){
//        ErrorMessage message = new ErrorMessage(HttpStatus.SERVICE_UNAVAILABLE,
//                deleteException.getMessage());
//        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(message);
//    }



    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<ErrorMessage> recordNotFoundException(RecordNotFoundException recordNotFoundException,WebRequest request){
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND,
                recordNotFoundException.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }



    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorMessage> userExistException(UserException userException,WebRequest request){
        ErrorMessage message = new ErrorMessage(HttpStatus.CONFLICT,
                userException.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }

}
