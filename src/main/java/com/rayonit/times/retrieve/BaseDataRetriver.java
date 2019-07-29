package com.rayonit.times.retrieve;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rayonit.times.configuration.HierarchicConfiguration;
import com.rayonit.times.model.BaseTsObject;
import com.rayonit.times.model.Bucket;
import com.rayonit.times.model.ParentBucket;
import com.rayonit.times.util.DateUtil;

import java.util.*;

public class BaseDataRetriver<T extends BaseTsObject> {
    private Gson gson;
    private Class<T> clazz;
    private ObjectMapper objectMapper;

    public BaseDataRetriver(Class<T> clazz) {
        this.gson = new Gson();
        this.clazz = clazz;
        this.objectMapper = new ObjectMapper();

    }

    protected List<T> extractAll(Bucket<T> bucket) {
        return bucket.getElements();
    }

    protected List<T> extractAll(ParentBucket<T> bucket, HierarchicConfiguration configuration) {
        return extractT(bucket.getItems(), configuration.getToLevel(), new ArrayList<>());
    }



    private List<T> extractFromMap(Map map, List<T> result) {
        for (Object innerMap : map.values()) {
            if (innerMap != null && innerMap instanceof LinkedHashMap) {
                extractFromMap((LinkedHashMap) innerMap, result);
            } else {
                if (innerMap != null && innerMap instanceof Map) {
                    for (Object objectMap : ((Map) innerMap).values()) {
                        if (objectMap instanceof LinkedHashMap) {
                            //String jsonString = new Gson().toJson(objectMap, LinkedHashMap.class);
                            result.add(objectMapper.convertValue(objectMap, clazz));
                        }
                    }
                }

            }
        }
        return result;
    }

    private List<T> extractT(Object object, int level, List<T> list) {
        if (level == 0) {
            if (object != null) {
                list.add(objectMapper.convertValue(object, clazz));
            }
        } else if (object instanceof Map) {
            level -= 1;
            for (Object innerObject : ((Map) object).values()) {
                if (object instanceof Map) {
                    extractT(innerObject, level, list);
                }
            }
        }
        return list;

    }

    protected List<T> extractWithRange(ParentBucket parentBucket,Date start,Date end,int level){
        Integer[] startArr = DateUtil.convertDateToArray(start);
        Integer[] endArr = DateUtil.convertDateToArray(end);
        return extractTInnerValues(parentBucket.getItems(), level, new ArrayList<>(), startArr, endArr, new Integer[level+1]);
    }

    private List<T> extractTInnerValues(Object object, int level, List<T> list,Integer[] start,Integer[] end,Integer[] current) {
        if (level == 0) {
            if (object != null) {
                if(isBetween(start, end, current,2))
                list.add(objectMapper.convertValue(object, clazz));
            }
        } else if (object instanceof Map) {
            level -= 1;
            Map map = (Map)object;
            for(Object key: map.keySet() ){
                current[level] = Integer.valueOf((String) key);
                if (object instanceof Map) {
                    extractTInnerValues(map.get(key), level, list, start, end,current);
                }
            }
        }
        return list;

    }

    private boolean isBetween(Integer[] start, Integer[] end, Integer[] current,int level){
        if(compareLevels(start, end, current, level)){
            return true;
        } else {
            if(start[level].equals(current[level]) || current[level].equals(end[level])){
                if(level>0)
                return isBetween(start, end, current, --level);
            }
        }
        return false;



    }

    //unaminisht
    private boolean compareLevels(Integer[] start, Integer[] end, Integer[] current,int level){
         return current[level]>start[level] && current[level]<end[level];
    }

}
