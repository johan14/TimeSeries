package com.rayonit.times.configuration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PersisterConfiguration {

    private String database;

    private String collection;

    private Integer time;

    public PersisterConfiguration() {
    }

    public PersisterConfiguration(String database, String collection, Integer time) {
        this.database = database;
        this.collection = collection;
        this.time = time;
    }
}
