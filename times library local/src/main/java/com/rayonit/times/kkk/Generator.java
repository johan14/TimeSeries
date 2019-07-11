package com.rayonit.times.kkk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class Generator {


    private TestObject testObject;
    @Autowired
    private MongoTemplate temp;
    private Map map;


    @PostConstruct
    public void generate() {
        int noDocs1=86000;
        int noDocs=noDocs1*1000;
        temp.dropCollection("DocPerEvent");
        long date = new Date().getTime();
        long fraction = (date + noDocs) / 10;
        for (long i = date; i < date + noDocs; i += 1000) {
            testObject = new TestObject("myName", new Date(i));
            temp.save(testObject, "DocPerEvent");

            if (i % fraction == 0)
                System.out.print("#");
        }
        long date1 = new Date().getTime();
        System.out.println("#####################################################################");
        System.out.println("DOC/EVENT");
        System.out.println("#####################################################################");
        System.out.println("Koha per insert: "+ ((date1-date)/86000)+"ms");
        System.out.print("\n\n\nKoha start per retrieve findAll DocPerEvent: " + new Date(date1));
        temp.findAll(TestObject.class, "DocPerEvent");
        System.out.print("\nKoha per retrieve: " + (new Date().getTime() - date1) + "ms\nList size: ");


        long date2 = new Date().getTime();
        System.out.print("\nKoha start per retrieve findbyRange DocPerEvent: " + new Date(date2));
        List<TestObject> list2 = temp.find(new Query().addCriteria(Criteria.where("created").gt(new Date(1562314782433L)).lt(new Date(1562400757433L))),TestObject.class, "DocPerEvent");
        System.out.print("\nKoha per retrieve: " + (new Date().getTime() - date1) + "ms\n\nList size: " + list2.size() + "\n\n");
    }


}