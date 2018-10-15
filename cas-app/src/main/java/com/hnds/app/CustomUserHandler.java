package com.hnds.app;

import com.hnds.client.session.GlobalUserHandler;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.stereotype.Component;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/10/12 0012 18:15
 */
@Component
public class CustomUserHandler implements GlobalUserHandler {

    @Override
    public void globalUserHandler(Assertion assertion) {
        System.out.println("");
//        AssertionHolder.getAssertion();
    }

}
