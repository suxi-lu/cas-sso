package com.hnds.client.session;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/25 0025 9:11
 */
public abstract class BaseGlobalRestTgtManager<T> implements GlobalRestTgtManager<T> {

    private Map<String, T> TGT_CACHE = Collections.synchronizedMap(new HashMap<String, T>());

    @Override
    public void add(String key, T value) {
        TGT_CACHE.put(key, value);
    }

    @Override
    public T get(String key) {
        return TGT_CACHE.get(key);
    }

    @Override
    public void remove(String key) {
        if (TGT_CACHE.containsKey(key)) {
            TGT_CACHE.remove(key);
        }
    }

}
