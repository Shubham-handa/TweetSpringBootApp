package com.tweetapp.model.Sequence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document(collection = "database_sequence")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatabaseSequence {


    @Id
    private String id;

    private long seq;
}
