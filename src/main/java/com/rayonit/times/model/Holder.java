package com.rayonit.times.model;

import java.util.Date;

public class Holder<T> {

    private Date timeStamp;
    private T payload;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public Holder(Date timeStamp, T payload) {
        this.timeStamp = timeStamp;
        this.payload = payload;
    }
}
