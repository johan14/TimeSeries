package com.rayonit.times.kkk;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.rayonit.times.model.BaseTsObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "created",
        "createdDate"
})
public class TestObject implements BaseTsObject {

    @JsonProperty("name")
    private String name;
    @JsonProperty("created")
    private Date created;
    private Integer pushed;
    private String alarmLevel;
    @JsonProperty("createdDate")
    private Date createdDate;
    private boolean virtual;
    private Date modified;

    public TestObject() {
    }

    public TestObject(String name, Date created) {
        this.name = name;
        this.created = created;
        this.virtual = true;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("created")
    public Date getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(Date created) {
        this.created = created;
    }

    public Integer getPushed() {
        return pushed;
    }

    public void setPushed(Integer pushed) {
        this.pushed = pushed;
    }

    public String getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(String alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    @JsonProperty("createdDate")
    public Date getCreatedDate() {
        return createdDate;
    }

    @JsonProperty("createdDate")
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isVirtual() {
        return virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }


    @Override
    public Date getCreationDate() {
        return created;
    }

    @Override
    public Map<String, Object> getPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        return payload;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "name='" + name + '\'' +
                ", created=" + created +
                '}';
    }
}
