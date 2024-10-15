package com.tweetapp.service;

import com.tweetapp.model.Sequence.DatabaseSequence;
import com.tweetapp.model.Sequence.UserSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class SequenceGeneratorSequence {


    private MongoOperations mongoOperations;

    @Autowired
    public SequenceGeneratorSequence(MongoOperations mongoOperations){
        this.mongoOperations = mongoOperations;
    }

    public long generateSequence(String seqName){
        DatabaseSequence ds = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class);

        return !Objects.isNull(ds) ? ds.getSeq() : 1;
    }


    public long generateUserSequence(String seqName){
        UserSequence us = mongoOperations.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq",1), FindAndModifyOptions.options().returnNew(true).upsert(true),
                UserSequence.class);

        return !Objects.isNull(us) ? us.getSeq() : 1;
    }
}
