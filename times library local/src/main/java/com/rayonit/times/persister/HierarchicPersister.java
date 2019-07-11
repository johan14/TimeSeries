package com.rayonit.times.persister;

import com.mongodb.client.result.UpdateResult;
import com.rayonit.times.configuration.HierarchicConfiguration;
import com.rayonit.times.configuration.PersisterConfiguration;
import com.rayonit.times.hierarchic.exception.InvalidPersisterConfigurationException;
import com.rayonit.times.model.BaseTsObject;
import com.rayonit.times.model.ParentBucket;
import com.rayonit.times.util.DateUtil;
import com.rayonit.times.util.ParentBucketUtil;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HierarchicPersister<T extends BaseTsObject> implements Persister<T> {

    private final MongoTemplate mongoTemplate;

    private HierarchicConfiguration persisterConfiguration;

    private List<Document> parentBucketIds;

    public HierarchicPersister(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void init(PersisterConfiguration config) {
        if (config instanceof HierarchicConfiguration) {
            this.persisterConfiguration = (HierarchicConfiguration) config;
            parentBucketIds = ParentBucketUtil.findAllParentBucketIds(persisterConfiguration, mongoTemplate);
        }

    }

    private void getAllIds(){}

    private List findAllParentBucketIds(PersisterConfiguration configuration) {
        Query query = new Query();
        query.fields().include("_id");
        query.fields().include("timeStamp");
        return mongoTemplate.find(query, Document.class, configuration.getCollection());
    }

    @Override
    public void insert(T t) {
        Update update = new Update();
        update = update.set(findKeyToPushElements(t.getCreationDate()), t);
        Query query = ParentBucketUtil.findParentBucket2(t.getCreationDate(), persisterConfiguration, mongoTemplate, parentBucketIds);
        if (query == null) {
            addBucket(t);
            parentBucketIds= ParentBucketUtil.findAllParentBucketIds(persisterConfiguration,mongoTemplate);
        } else {
            UpdateResult updateResult = this.mongoTemplate.updateFirst(query, update, persisterConfiguration.getCollection());
        }
    }


    private String findKeyToPushElements(Date date) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        StringBuilder stringBuilder = new StringBuilder();
        switch (persisterConfiguration.getHierarchicLevel()) {
            case DAY_LEVEL:
                if (persisterConfiguration.getToLevel() == 3)
                    stringBuilder.append("items.").append(hour).append(".").append(minute).append(".").append(second);
                else if (persisterConfiguration.getToLevel() == 2)
                    stringBuilder.append("items.").append(hour).append(".").append(minute);
                else if (persisterConfiguration.getToLevel() == 1)
                    stringBuilder.append("items.").append(hour);
                break;
            case HOUR_LEVEL:
                if (persisterConfiguration.getToLevel() == 2)
                    stringBuilder.append("items.").append(minute).append(".").append(second);
                else if (persisterConfiguration.getToLevel() == 1)
                    stringBuilder.append("items.").append(minute);
                break;
            case MINUTE_LEVEL:
                if (persisterConfiguration.getToLevel() == 1)
                    stringBuilder.append("items.").append(second);
                break;
            default:
                throw new InvalidPersisterConfigurationException("Wrong hierarchic level configuration in update." +
                        "Please provide another configuration.");
        }
        return stringBuilder.toString();
    }

    private void addBucket(T t) {
        ParentBucket<T> parentBucket = new ParentBucket<>(t.getCreationDate(), this.persisterConfiguration.getHierarchicLevel(), this.persisterConfiguration.getToLevel());
        parentBucket.addItem(t.getCreationDate(), t);
        mongoTemplate.save(parentBucket, persisterConfiguration.getCollection());
    }

}
