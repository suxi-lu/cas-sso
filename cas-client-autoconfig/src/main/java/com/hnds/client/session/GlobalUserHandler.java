package com.hnds.client.session;

import org.jasig.cas.client.validation.Assertion;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/18 0018 11:31
 */
public interface GlobalUserHandler {

    void globalUserHandler(Assertion assertion);

}
