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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PersisterV2<T extends BaseTsObject> {

    private final MongoTemplate mongoTemplate;
    private BucketV2 bucketV2;
    private List<Document> bucketIds;
    private PersisterConfiguration persisterConfiguration;
    private Update update;
    private List <AggregationOptions> aggregationOptions;
    private Field aggregationField;

    public PersisterV2(MongoTemplate mongoTemplate, String collection) {
        this.mongoTemplate = mongoTemplate;
        bucketIds = new ArrayList<>();
        persisterConfiguration = new PersisterConfiguration();
        persisterConfiguration.setCollection(collection);
        bucketIds = findBucketIds();
        update = new Update();
        aggregationOptions = null;
    }

    public PersisterV2(MongoTemplate mongoTemplate, String collection, List<AggregationOptions> aggregationOptions) {
        this(mongoTemplate, collection);
        this.aggregationOptions = aggregationOptions;
    }

    public void insert(T item) {
        insertRaw(item, item.getCreationDate());
    }

    public void insertOnlyPayload(T item) {
        insertRaw(item.getPayload(), item.getCreationDate());
    }
    
    public void insertAndAggregateByField(T item,String fieldName){
        try {
            aggregationField =  item.getClass().getDeclaredField(fieldName);
            aggregationField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        insertRaw(item, item.getCreationDate());
    }



    public void insertRaw(Object item, Date date)  {
        Object object = null;
        if(aggregationField!=null) {
            try {

                object = aggregationField.get(item);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        else
            object = item;

        Holder<Object> holder = new Holder<>(date, item);
        if (aggregationOptions != null) {
            update.push("items", holder);
            for(AggregationOptions aggregationOption : aggregationOptions)
                switch (aggregationOption) {
                    case AVG:
                        update.inc("sum_samples", (Number)object).inc("no_samples", 1);
                        break;
                    case MAX:
                        update.max("max", object);
                        break;
                    case MIN:
                        update.min("min", object);
                        break;
                }
        } else
            update.push("items", holder);

        Query query = findCurrentBucket(date);

        if (query == null) {
            createNewBucket(date);
            insertRaw(item, date);
        } else
            mongoTemplate.updateFirst(query, update, persisterConfiguration.getCollection());
    }


    private void createNewBucket(Date date) {
        BucketV2 bucketV2 = new BucketV2(DateUtil.setTimeHierarchic(date, HierarchicLevel.HOUR_LEVEL, false));
        mongoTemplate.save(bucketV2, persisterConfiguration.getCollection());
        bucketIds = findBucketIds();
    }

    public List<Document> findByRange(Date start, Date end) {

        return mongoTemplate.find(new Query().addCriteria(Criteria.where("startTime").gte(start).and("endTime").lte(end)), Document.class,
                persisterConfiguration.getCollection());
    }

    public List findByRange(Date start, Date end, AggregationOptions aggregationOptions) {
        List result;
        Query query = new Query();
        if (aggregationOptions != AggregationOptions.AVG) {

            query.addCriteria(Criteria.where("startTime").gte(start).and("endTime").lte(end).and(aggregationOptions.toString().toLowerCase()).exists(true))
                    .fields().include("startTime").include(aggregationOptions.toString().toLowerCase()).exclude("_id");
            result = mongoTemplate.find(query, Object.class,
                    persisterConfiguration.getCollection());
        } else {
            query.addCriteria(Criteria.where("startTime").gte(start).and("endTime").lte(end).and("no_samples").exists(true))
                    .fields().include("startTime").include("sum_samples").include("no_samples").exclude("_id");
            result = calculateAverages(mongoTemplate.find(query, Document.class, persisterConfiguration.getCollection()));

        }

        return result;
    }

    private List calculateAverages(List<Document> input) {

        for (Document document : input) {
            Double res =((Number)document.get("sum_samples")).doubleValue()/  ((Number)document.get("no_samples")).doubleValue();
            document.append("avg", res);
            document.remove("sum_samples");
            document.remove("no_samples");
        }
        return input;
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
