package com.tweetapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.bytebuddy.implementation.bind.annotation.Default;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.List;


@Data
@Document(collection = "user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {



    @Id
    @NotEmpty(message = "Please enter the username and it doesn't match with email Id")
    private String loginId;

    @NotEmpty(message = "Please enter the first name")
    @Pattern(regexp = "^[a-zA-Z]+$",message = "Invalid First Name")
    private String firstName;

    @NotEmpty(message = "Please enter the last name")
    @Pattern(regexp = "^[a-zA-Z]+$",message = "Invalid Last Name")
    private String lastName;

    @NotEmpty(message = "Please enter the Date of Birth and it should be YYYY-MM-DD format")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12]\\d|3[01])$",message = "Please enter DOB in YYYY-MM-DD format")
    private String dob;

    private String userProfilePictureFileName;

    private Binary profilePicture;

    @NotEmpty(message = "Please enter the gender as male, female and other")
    @Pattern(regexp = "male|female|other",message = "Invalid gender" )
    private String gender;

    @NotEmpty(message = "Please enter the email")
    @Pattern(regexp = "^[a-zA-z0-9+_.-]+@[a-zA-Z0-9.-]+$", message = "Invalid EmailID")
    private String email;

    @NotEmpty(message = "Please enter the password")
    private String password;

    @NotEmpty(message = "Please enter the confirm password")
    private String confirmPassword;

    @NotEmpty(message = "Please enter the contact number")
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$")
    private String contactNumber;

    private String accountCreationDate;

    private boolean verified;

    private List<Tweet> allTweets;
}
