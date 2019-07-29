package com.rayonit.times.retrieve;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DataRetriever<T> {

    List<T> findAll();

    List<T> findByRange(Date start, Date end);

    Map<Date, T> findAllAsMap();

    Map<Date, T> findByRangeAsMap(Date start, Date end);




}
