package com.rayonit.times.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reading implements BaseTsObject {

    private String device;
    private String deviceId;
    private Long pushed;
    private String name;
    private String value;
    private String alarmLevel;


    public Reading(String device, Long created) {
        this.device = device;

    }

    @Override
    public String toString() {
        return "Reading{" +
                "device='" + device + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", pushed=" + pushed +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", alarmLevel='" + alarmLevel + '\'' +
                '}';
    }



    public Date getCreationDate() {
        return new Date();
    }

    @Override
    public Map<String, Object> getPayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", name);
        payload.put("value", name);
        return payload;
    }
}
