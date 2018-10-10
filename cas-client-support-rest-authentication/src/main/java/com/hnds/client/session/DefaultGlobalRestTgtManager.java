package com.hnds.client.session;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/21 0021 13:42
 */
public class DefaultGlobalRestTgtManager extends BaseGlobalRestTgtManager<String> {

    @Override
    public void addTgt(String key, String tgt) {
        add(key, tgt);
    }

    @Override
    public String getTgt(String key) {
        return get(key);
    }

    @Override
    public String removeTgt(String key) {
        String tgt = getTgt(key);
        remove(key);
        return tgt;
    }

}
