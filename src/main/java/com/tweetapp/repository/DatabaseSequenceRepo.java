package com.tweetapp.repository;

import com.tweetapp.model.Sequence.DatabaseSequence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatabaseSequenceRepo extends MongoRepository<DatabaseSequence,String> {
}
