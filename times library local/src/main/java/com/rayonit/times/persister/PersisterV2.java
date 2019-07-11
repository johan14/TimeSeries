package com.rayonit.times.persister;

import com.rayonit.times.configuration.PersisterConfiguration;
import com.rayonit.times.hierarchic.levels.AggregationOptions;
import com.rayonit.times.hierarchic.levels.HierarchicLevel;
import com.rayonit.times.model.BaseTsObject;
import com.rayonit.times.model.BucketV2;
import com.rayonit.times.model.Holder;
import com.rayonit.times.util.DateUtil;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersisterV2<T extends BaseTsObject> {

    private final MongoTemplate mongoTemplate;
    private BucketV2<T> bucketV2;
    private List<Document> bucketIds;
    private PersisterConfiguration persisterConfiguration;
    private Update update;
    private AggregationOptions aggregationOption;

    public PersisterV2(MongoTemplate mongoTemplate, String collection) {
        this.mongoTemplate = mongoTemplate;
        bucketIds = new ArrayList<>();
        persisterConfiguration = new PersisterConfiguration();
        persisterConfiguration.setCollection(collection);
        bucketIds = findBucketIds();
        update = new Update();
        aggregationOption = null;
    }

    public PersisterV2(MongoTemplate mongoTemplate, String collection, AggregationOptions aggregationOption) {
        this(mongoTemplate, collection);
        this.aggregationOption = aggregationOption;


    }

    public void insert(T item) {
        insertRaw(item, item.getCreationDate());
    }

    public void insertRaw(Object item, Date date) {

        Holder<Object> holder = new Holder<>(date, item);
        if (aggregationOption != null) {
            switch (aggregationOption) {
                case AVG:
                    update.push("items", holder).inc("sum_samples", (Number) holder.getPayload()).inc("no_samples", 1);
                    break;
                case MAX:
                    update.push("items", holder).max("max", holder.getPayload());
                    break;
                case MIN:
                    update.push("items", holder).min("min", holder.getPayload());
                    break;
            }
        } else
            update.push("items", holder);

        Query query = findCurrentBucket(date);

        if (query == null) {
            BucketV2 bucketV2 = new BucketV2(DateUtil.setTimeHierarchic(date, HierarchicLevel.HOUR_LEVEL, false));
            mongoTemplate.save(bucketV2, persisterConfiguration.getCollection());
            bucketIds = findBucketIds();
            insertRaw(item, date);


        } else
            mongoTemplate.updateFirst(query, update, persisterConfiguration.getCollection());
    }

    private List findByRange(Date start, Date end) {

        return mongoTemplate.find(new Query().addCriteria(Criteria.where("startTime").gte(start).and("endTime").lte(end)), PersisterV2.class,
                persisterConfiguration.getCollection());
    }

    public List findByRange(Date start, Date end, AggregationOptions aggregationOptions) {
        List result;
        Query query = new Query();
        if (aggregationOptions != AggregationOptions.AVG) {

            query.addCriteria(Criteria.where("startTime").gte(start).and("endTime").lte(end))
                    .fields().include(aggregationOptions.toString().toLowerCase());
            result = mongoTemplate.find(query, PersisterV2.class,
                    persisterConfiguration.getCollection());
        } else {
            query.addCriteria(Criteria.where("startTime").gte(start).and("endTime").lte(end))
                    .fields().include("sum_samples").include("no_samples");
            result = calculateAverages(mongoTemplate.find(query, Document.class, persisterConfiguration.getCollection()));

        }

        return result;
    }

    private List calculateAverages(List<Document> input){
        List<Double> results = new ArrayList<>();

        for (Document document: input){
            Double res = (Double)document.get("sum_samples") / (Integer) document.get("no_samples");
            results.add(res);
        }
        return results;
    }




    private List<Document> findBucketIds() {
        Query query = new Query();
        query.fields().include("_id");
        query.fields().include("startTime");
        query.fields().include("endTime");

        bucketIds = mongoTemplate.find(query, Document.class, persisterConfiguration.getCollection());
        return bucketIds;

    }

    private Query findCurrentBucket(Date timestamp) {

        for (Document bucketId : bucketIds) {
            if (timestamp.after(((Date) bucketId.get("startTime"))) && timestamp.before(((Date) bucketId.get("endTime")))) {
                Query query = new Query();
                query.addCriteria(Criteria.where("_id").is(bucketId.get("_id")));
                return query;
            }
        }
        return null;
    }

}
