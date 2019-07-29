package com.rayonit.times.configuration;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;


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

    public @Bean
    MongoTemplate mongoTemplate() throws Exception {

        //remove _class
        MappingMongoConverter converter =
                new MappingMongoConverter(mongoDbFactory(), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory(), converter);

        return mongoTemplate;

    }

    public @Bean
    MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(mongoClient(), getDatabaseName());
    }

    public String getCollection() {
        return collection;
    }


}


