package com.rayonit.times.model;

import java.util.Date;
import java.util.Map;

public interface BaseTsObject {

    Date getCreationDate();

    Map<String, Object> getPayload();
}
