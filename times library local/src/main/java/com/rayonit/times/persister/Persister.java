package com.rayonit.times.persister;

import com.rayonit.times.configuration.PersisterConfiguration;
import com.rayonit.times.model.BaseTsObject;

public interface Persister<T extends BaseTsObject> {

    void init(PersisterConfiguration config);

    void insert(T t);


}
