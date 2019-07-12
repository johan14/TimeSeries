package com.rayonit.times;


import com.rayonit.times.hierarchic.levels.AggregationOptions;
import com.rayonit.times.kkk.DemoConfig;
import com.rayonit.times.kkk.TestObject;
import com.rayonit.times.persister.PersisterV2;
import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

    private final MongoTemplate mongoTemplate;
    private final DemoConfig demoConfig;


    public DemoApplication(MongoTemplate mongoTemplate, DemoConfig demoConfig) {
        this.mongoTemplate = mongoTemplate;
        this.demoConfig = demoConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @PostConstruct
    void test() {

//        HierarchicPersister<TestObject> hierarchicPersister = new HierarchicPersister<>(mongoTemplate);
//        HierarchicConfiguration hiearchicConfiguration = new HierarchicConfiguration(HierarchicLevel.DAY_LEVEL, BaseLevel.SECOND,demoConfig.getCollection());
//        hierarchicPersister.init(hiearchicConfiguration);
//
//        boolean generateData = false;
//
//        long date1 = new Date(1562198400000L).getTime();
//        long date2 = date1;
//        System.out.println("#####################################################################");
//        System.out.println("TIMESERIES");
//        System.out.println("#####################################################################");
//        if(generateData) {
//            int noDocs = 86000;
//            for (float i = 0; i < noDocs; i++) {
//                long date9 = new Date().getTime();
//                hierarchicPersister.insert(new TestObject("myName", new Date(date1)));
////            System.out.print((new Date().getTime()-date9)+"%   \r");
//                System.out.print(((i / noDocs) * 100) + "%   \r");
//                System.out.printf("%.2f%% \r", ((i / noDocs) * 100));
//                date1 += 1000;
//            }
//            long date3 = new Date().getTime();
//            System.out.println("\nStart insert date: " + new Date(date2));
//            System.out.println("Koha e mbarimit per insert: " + new Date());
//            System.out.println(((date3 - date2) / noDocs) + " ms/update");
//        }
//
//        SimpleDataRetriever<TestObject> simpleDataRetriever = new SimpleDataRetriever<>(mongoTemplate,hiearchicConfiguration,TestObject.class);
//        long start = new Date().getTime();
//
//        System.out.print("\nFillimi i retrieve findAll NonNULL: "+ new Date(start) );
//        List<TestObject> list =simpleDataRetriever.findAll();
//        System.out.print("\nKoha e retrieve findAll NonNULL eshte: "+(new Date().getTime()-start)+ "ms\n"+ "Numri i dokumentave: "+ list.size() +"\n");
//
//
//        long date4 = new Date().getTime();
//        System.out.print("\n\nKoha e retrieve byRange :"+ new Date());
//        List<TestObject> list7= simpleDataRetriever.findByRange(new Date(1562198400000L),new Date(1562279220000L));
//        System.out.print("\nKoha e mbarimit per retrieve ranged: "+ (new Date().getTime()-date4)+" ms\nList size: "+ list7.size()+"\n");
//
//        /////////////PERSISTER V2
        boolean generate = false;

        if (generate){
            mongoTemplate.dropCollection("simpleColl");
            mongoTemplate.dropCollection("rawNoPojo");}
        PersisterV2<TestObject> noPojo = new PersisterV2<>(mongoTemplate, "simpleColl", AggregationOptions.AVG);
        long dateTest =1562883686921L;
        long date1 = new Date().getTime();
        int noDocs = 3600 * 24;
        long date3 = new Date().getTime();
        if (generate)
            for (float i = 0; i < noDocs; i++) {
                //noPojo.insert(new TestObject("myName", new Date(date1)));
                noPojo.insertRaw(i, new Date(date1));
                Map hash = new HashMap<>();
                hash.put("payload",i);
                hash.put("timeStamp",new Date(date1));
                mongoTemplate.save(hash,"rawNoPojo");
                System.out.printf("%.2f%% \r", ((i / noDocs) * 100));
                date1 += 1000;
            }

        long date5 = new Date().getTime();
        System.out.println(((date5 - date3) / noDocs));

        long date2 = new Date().getTime();
        noPojo.findByRange(new Date(dateTest), new Date(dateTest + (3600000 * 3)));
        System.out.println("FindByRange time: "+ (new Date().getTime()-date2));
        long date6 = new Date().getTime();
        mongoTemplate.find(new Query().addCriteria(Criteria.where("timeStamp").gte(new Date(dateTest)).lte( new Date(dateTest + (3600000 * 3)))),
                Document.class,"rawNoPojo");
        System.out.println("FindByRange time NO BUCKET: "+(new Date().getTime()-date6) );
        List list = noPojo.findByRange(new Date(dateTest), new Date(dateTest + (3600000 * 3)), AggregationOptions.AVG);
        System.out.println(list);

//    private List<Integer> extractFromMap(List<Map> maps ){
//        List<Integer> result = new ArrayList<>();
//        for(Object innerMap : maps){
//            if(innerMap instanceof Map &&((Map) innerMap).get("0") != null && ((Map) innerMap).get("0") instanceof Map){
//                return  result.addAll(extractFromMap((Map) innerMap));
//            } else {
//                return new ArrayList<Integer>(((Map)innerMap).values());
//            }
//        }
//    }


    }
}


