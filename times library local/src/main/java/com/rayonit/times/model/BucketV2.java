package com.rayonit.times.model;

import com.rayonit.times.hierarchic.levels.BaseLevel;
import com.rayonit.times.hierarchic.levels.HierarchicLevel;
import com.rayonit.times.util.DateUtil;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;
@Data
public class BucketV2<T extends BaseTsObject> {

    private Date startTime;

    private Date endTime;

    private List<Holder<T>> items;

    public BucketV2(Date startTime) {
        this.startTime = startTime;
        endTime = DateUtil.addHour(startTime);
        this.items = new ArrayList<>();
    }


}