package com.rayonit.times.model;

import com.rayonit.times.hierarchic.levels.BaseLevel;

import java.util.Date;

public class HierarchicBucket<T>  extends Bucket<T>{

    private BaseLevel level;

    public HierarchicBucket(Date start, Date end, BaseLevel level) {
        super(start, end);
        this.level = level;
    }

    public HierarchicBucket(BaseLevel level) {
        this.level = level;
    }

    public BaseLevel getLevel() {
        return level;
    }

    public void setLevel(BaseLevel level) {
        this.level = level;
    }
}
