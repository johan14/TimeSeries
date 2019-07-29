package com.rayonit.times.configuration;

import com.rayonit.times.hierarchic.exception.InvalidPersisterConfigurationException;
import com.rayonit.times.hierarchic.levels.BaseLevel;
import com.rayonit.times.hierarchic.levels.HierarchicLevel;
import lombok.Data;

@Data
public class HierarchicConfiguration extends PersisterConfiguration {

    private final HierarchicLevel hierarchicLevel;
    private final int toLevel;

    public HierarchicConfiguration(HierarchicLevel hierarchicLevel, BaseLevel baseLevel, String collection) {
        super();
        this.hierarchicLevel = hierarchicLevel;
        if(findIndex(baseLevel)==-5)
           throw new InvalidPersisterConfigurationException("Wrong configuration");
        else
        this.toLevel = findIndex(baseLevel);
        this.setCollection(collection);
    }


    private int findIndex(BaseLevel baseLevel) {
        int i = 0;
        for (BaseLevel base : this.hierarchicLevel.getLevels()) {
            if (base.equals(baseLevel)) {
                return i;
            }
            ++i;
        }
        return -5;
    }

}
