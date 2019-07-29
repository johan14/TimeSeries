package com.rayonit.times.persister;

import com.rayonit.times.configuration.MongoConf;
import com.rayonit.times.configuration.PersisterConfiguration;
import com.rayonit.times.model.BaseTsObject;
import com.rayonit.times.model.Bucket;
import com.rayonit.times.util.DateUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Calendar;
import java.util.Date;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

public class SimplePersister<T extends BaseTsObject> implements Persister<T> {

    private Bucket<T> bucket;
    private MongoTemplate mongoTemplate;
    private String collection;
    private MongoConf mongoConf;
    private PersisterConfiguration persisterConfiguration;


    public SimplePersister(MongoConf mongoConf) {

        this.mongoConf = mongoConf;

        try {
            this.mongoTemplate = mongoConf.mongoTemplate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        persisterConfiguration = PersisterConfiguration.builder()
                .collection(mongoConf.getCollection())
                .database(mongoConf.getDatabaseName())
                .time(mongoConf.getTime())
                .build();
        this.init(persisterConfiguration);
    }

    @Override
    public void init(PersisterConfiguration config) {
            this.collection = config.getCollection();
    }

    private void createNewBucket(Date startTime) {
        this.bucket = new Bucket<>(startTime, addHoursToJavaUtilDate(startTime, mongoConf.getTime()));
        this.bucket = this.mongoTemplate.insert(bucket, collection);
    }

    @Override
    public void insert(T t) {

        Update update = new Update();
        update = update.push("elements", t.getPayload());
        if (mongoTemplate.find(findBucket(t), Bucket.class, mongoConf.getCollection()).isEmpty()) {
            Date d = DateUtil.setTimeOfDateToTime(t.getCreationDate(), mongoConf.getTime());
            createNewBucket(d);
        }
        mongoTemplate.updateFirst(findBucket(t), update, collection);

    }

    public Date addHoursToJavaUtilDate(Date date, int timeAdded) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MILLISECOND, timeAdded);
        return calendar.getTime();
    }

    private Query findBucket(T t) {
        Query query = new Query();
        query.addCriteria(Criteria.where("start").lte(t.getCreationDate()).and("end").gt(t.getCreationDate()));
        System.out.println("Query => " + query.toString());
        return query;
    }

    public void get(Date start, Date end, String field) {

        Aggregation agg = newAggregation(
                project("start").andExclude("_id")
        );

        AggregationResults<?> group = mongoTemplate.aggregate(agg, mongoConf.getCollection(), Class.class);
        Object result = group.getRawResults();
        System.out.println("Rezultati:   " + group.getMappedResults());
    }

    public PersisterConfiguration getConfiguration(){
       return this.persisterConfiguration;
    }
}
