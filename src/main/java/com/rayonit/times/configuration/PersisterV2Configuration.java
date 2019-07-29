package com.rayonit.times.configuration;

import com.rayonit.times.hierarchic.levels.AggregationOptions;

import java.util.List;

public class PersisterV2Configuration {
    private List<AggregationOptions> aggregationOptionsList;

    public PersisterV2Configuration(List<AggregationOptions> aggregationOptionsList) {
        this.aggregationOptionsList = aggregationOptionsList;
    }
}
