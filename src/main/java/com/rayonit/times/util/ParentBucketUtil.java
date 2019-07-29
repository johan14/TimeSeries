package com.rayonit.times.util;

import com.rayonit.times.configuration.HierarchicConfiguration;
import com.rayonit.times.configuration.PersisterConfiguration;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ParentBucketUtil {

    public static Query findParentBucket(Date date, MongoTemplate mongoTemplate, PersisterConfiguration config) {


        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);


        // TODO add logic to find proper Parent Bucket stored in Database

        ProjectionOperation projectionOperation = project()
                .andExpression("year(timeStamp)").as("year")
                .andExpression("month(timeStamp)").as("month")
                .andExpression("dayOfMonth(timeStamp)").as("day");


        MatchOperation matchOperation = match(
                new Criteria("year").is(year)
                        .and("month").is(month)
                        .and("day").is(day));


        Aggregation aggregation = newAggregation(projectionOperation, matchOperation);
        AggregationResults<?> aggregationResults = mongoTemplate.aggregate(aggregation, config.getCollection(), Object.class);
        Query query = new Query();
        if (!aggregationResults.getMappedResults().isEmpty()) {
            LinkedHashMap linkedHashMap = (LinkedHashMap) aggregationResults.getMappedResults().get(0);
            query.addCriteria(Criteria.where("_id").is(linkedHashMap.get("_id")));
//      mongoTemplate.find(new Query().fields().include("timeStamp"), Object.class, "parentBucket");
            return query;
        } else
            return null;
    }


    public static Query findParentBucket2(Date date, HierarchicConfiguration hierarchicConfiguration, MongoTemplate mongoTemplate, List<Document> parentBucketIds) {

        Date currentDate = DateUtil.setTimeHierarchic(date, hierarchicConfiguration.getHierarchicLevel(), false);
        for (Document document : parentBucketIds) {
            if (document.containsValue(currentDate)) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(document.get("_id")));
                return query;
            }

        }

        return null;
    }

    public static List findAllParentBucketIds(HierarchicConfiguration configuration, MongoTemplate mongoTemplate) {
        Query query = new Query();
        query.fields().include("_id");
        query.fields().include("timeStamp");

        List<Document> parentBucketsId = mongoTemplate.find(query, Document.class, configuration.getCollection());

        for (Document document : parentBucketsId)
            document.put("timeStamp", DateUtil.setTimeHierarchic((Date) document.get("timeStamp"), configuration.getHierarchicLevel(), false));

        return parentBucketsId;
    }


}
