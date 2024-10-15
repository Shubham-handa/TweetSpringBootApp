package com.tweetapp.repository;

import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User,String> {

    @Query("{ email : ?0}")
    User findByUserEmail(String email);

    @Query("{ loginId : ?0}")
    User findByUserName(String loginId);
}
