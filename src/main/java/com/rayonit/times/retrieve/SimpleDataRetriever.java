package com.rayonit.times.retrieve;

import com.rayonit.times.configuration.HierarchicConfiguration;
import com.rayonit.times.configuration.PersisterConfiguration;
import com.rayonit.times.model.BaseTsObject;
import com.rayonit.times.model.Bucket;
import com.rayonit.times.model.ParentBucket;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.*;

public class SimpleDataRetriever<T extends BaseTsObject> extends BaseDataRetriver<T> implements DataRetriever<T> {

    private final MongoTemplate mongoTemplate;
    private final PersisterConfiguration persisterConfiguration;

    public SimpleDataRetriever(MongoTemplate mongoTemplate, PersisterConfiguration persisterConfiguration, Class<T> clazz) {
        super(clazz);
        this.mongoTemplate = mongoTemplate;
        this.persisterConfiguration = persisterConfiguration;
    }

    @Override
    public List<T> findAll() {
        List<T> data = new ArrayList<>();
        if (!(persisterConfiguration instanceof HierarchicConfiguration)) {
            List<Bucket> buckets = mongoTemplate.findAll(Bucket.class, persisterConfiguration.getCollection());
            for (Bucket bucket : buckets) {
                data.addAll(bucket.getElements());
            }
            return data;
            //return buckets.stream().map((Function<Bucket, Object>) Bucket::getElements).collect(Collectors.toList());
        } else {
            System.out.println("\nStart QUERY time  => " + new Date());
            List<ParentBucket> buckets = mongoTemplate.findAll(ParentBucket.class, persisterConfiguration.getCollection());
            System.out.println("Start time  => " + new Date());
            for (ParentBucket bucket : buckets) {
                //TODO retrieve for the current Hiearchich level
                data.addAll(this.extractAll(bucket, (HierarchicConfiguration) persisterConfiguration));
            }
            System.out.println("End time  => " + new Date());

            return data;
        }
    }

    @Override
    public List<T> findByRange(Date start, Date end) {
        List<T> data = new ArrayList<>();
        if (!(persisterConfiguration instanceof HierarchicConfiguration)) {

        } else {

            List<ParentBucket> parentBuckets = mongoTemplate.findAll(ParentBucket.class, persisterConfiguration.getCollection());

            if(parentBuckets.size()==1){
                return  extractWithRange(parentBuckets.get(0),start, end, ((HierarchicConfiguration) persisterConfiguration).getToLevel());
            }else if(parentBuckets.size()==2){

            }
            // determine if one bucket , 2 ore more than 2

        }
        return new ArrayList<>();


    }

    @Override
    public Map<Date, T> findAllAsMap() {
        Map<Date, T> result = new HashMap<>();
        if (!(persisterConfiguration instanceof HierarchicConfiguration)) {
            List<Bucket> buckets = mongoTemplate.findAll(Bucket.class, persisterConfiguration.getCollection());
            for (Bucket<T> bucket : buckets) {
                for (T item : bucket.getElements()) {
                    result.put(item.getCreationDate(), item);
                }
            }
        } else {
//            List<ParentBucket> buckets = mongoTemplate.findAll(ParentBucket.class, persisterConfiguration.getCollection());
//            for(ParentBucket bucket : buckets){
//                //TODO retrieve for the current Hiearchich level
//                data.addAll(bucket.getItems().values());
//            }


        }
        return result;
    }

    @Override
    public Map<Date, T> findByRangeAsMap(Date start, Date end) {
        return null;
    }


}
