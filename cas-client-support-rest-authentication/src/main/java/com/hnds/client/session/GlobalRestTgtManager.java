package com.hnds.client.session;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/21 0021 13:19
 */
public interface GlobalRestTgtManager<T> {

    void add(String key, T value);

    T get(String key);

    void remove(String key);

    void addTgt(String key, String tgt);

    String getTgt(String key);

    String removeTgt(String key);

}
