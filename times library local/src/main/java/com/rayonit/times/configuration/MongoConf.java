package com.rayonit.times.configuration;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;


public class MongoConf extends AbstractMongoConfiguration {


    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${persister.collection}")
    private String collection;

    @Value("${time}")
    private Integer time;

    public Integer getTime() {
        return time;
    }

    @Override
    public String getDatabaseName() {
        return database;
    }

    @Bean
    public MongoClient mongoClient() {
        return new MongoClient();
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    public String getCollection() {
        return collection;
    }

}
