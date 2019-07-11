package com.rayonit.times.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class Bucket<T> {

    @Id
    private String id;
    private Date start;
    private Date end;
    private List<T> elements;


    public Bucket(Date start, Date end) {
        this.start = start;
        this.end = end;
        this.elements = new ArrayList<>();
    }

    public void addDocument(T t) {
        if (this.elements == null) {
            this.elements = new ArrayList<>();
        }
        this.elements.add(t);
    }
}
