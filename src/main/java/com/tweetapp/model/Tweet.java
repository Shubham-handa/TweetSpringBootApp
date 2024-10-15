package com.tweetapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tweet {


    @Transient
    public static final String SEQUENCE_NAME = "tweet_sequence";

    @Id
    private long tweetId;

    private String userID;

    @NotEmpty(message = "Please enter the tweet information")
    @Size(max = 144,message = "Maximum 144 characters")
    private String tweetMessage;

    private String tweetPublishedDate;

    @Size(max = 50, message = "Maximum 50 characters")
    private String tagDetail;

    private long usersLikeTweet;

    private String tweetPhotoFileName;

    private Binary tweetPhoto;

    private List<Tweet> usersReplyOnTweet;
}
