package com.tweetapp.repository;

import com.tweetapp.model.Tweet;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends MongoRepository<Tweet,String> {

    @Query("{ userID : ?0}")
    List<Tweet> findByUserName(String username);
}
