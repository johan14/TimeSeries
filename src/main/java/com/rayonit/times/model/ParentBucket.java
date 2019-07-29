package com.rayonit.times.model;

import com.rayonit.times.hierarchic.levels.BaseLevel;
import com.rayonit.times.hierarchic.levels.HierarchicLevel;
import com.rayonit.times.util.DateUtil;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document
public class ParentBucket<T> {

    private Date timeStamp;

    private HierarchicLevel level;

    private Map items;

    private int toLevel;

    public ParentBucket(Date timeStamp, HierarchicLevel level, int toLevel) {
        this.toLevel = toLevel;
        this.timeStamp = DateUtil.setTimeHierarchic(timeStamp,level,true);
        this.items = new HashMap();
        this.level = level;
        int i = 1;
        this.items = generateHolder(this.level.getLevels()[i], i, toLevel);
    }

    //TODO VALIDATE IF A BUCKET HAS BEEN CREATED FOR THAT DAY
    public void addItem(Date date, T item) {
        if (this.level.getLevels().length < 3) {
            Integer key = DateUtil.findKey(date, this.level.getLevels()[1]);
            items.put(key, item);
            //TODO add Update

        } else {
            insertToNested(date, item, this.items, 1);
        }
    }

    private void insertToNested(Date date, T item, Object holders, int levelStartFrom) {
        if (levelStartFrom < this.level.getLevels().length - 1) {
            if (holders instanceof Map) {
                Integer foundKey = DateUtil.findKey(date, this.level.getLevels()[levelStartFrom]);
                Object foundHolder = ((Map) holders).get(foundKey);
                insertToNested(date, item, foundHolder, ++levelStartFrom);
            }
        } else {
            if (holders instanceof Map) {
                Integer foundKey = DateUtil.findKey(date, this.level.getLevels()[levelStartFrom]);
                ((Map) holders).put(foundKey, item);
            }
        }
    }

    private Map<Integer, T> generateAtomicHolder(int max) {
        Map<Integer, T> result = new HashMap<>();
        for (int i = 0; i < max; i++) {
            result.put(i, null);
        }
        return result;
    }


    private Map generateHolder(BaseLevel level, int currLevel, int toLevel) {
        Map bigHolder = new HashMap();
        boolean generateAtomic = false;
        if (currLevel == toLevel - 1) {
            generateAtomic = true;
        }
        if (level == BaseLevel.MINUTE) {
            return generateUnitHolder(generateAtomic, currLevel, toLevel, level, 60, 60);
        } else if (level == BaseLevel.HOUR) {
            return generateUnitHolder(generateAtomic, currLevel, toLevel, level, 24, 60);
        }
        return bigHolder;
    }

    private Map generateUnitHolder(boolean generateAtomic, int currLevel, int toLevel, BaseLevel level, int out, int in) {
        BaseLevel currentLevel = level;
        if (!generateAtomic) {
            currLevel++;
            currentLevel = this.level.getLevels()[currLevel];
        }
        Map<Integer, Map<Integer, T>> holder = new HashMap<>();
        for (int i = 0; i < out; i++) {
            if (generateAtomic)
                holder.put(i, generateAtomicHolder(in));
            else {
                holder.put(i, generateHolder(currentLevel, currLevel, toLevel));
            }

        }
        return holder;
    }

    public Map getItems() {
        return items;
    }
}
