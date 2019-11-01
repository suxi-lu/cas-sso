package com.learn.client.utils;

import org.jasig.cas.client.validation.Assertion;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/18 0018 12:20
 */
public class CustomCommonUtils {

    /**
     * 获取当前登陆用户信息
     * @param request
     * @return
     */
    public static Assertion getAssertion(final HttpServletRequest request) {
        final HttpSession session = request.getSession(true);
        return getAssertion(session);
    }

    /**
     * 获取当前登陆用户信息
     * @param session
     * @return
     */
    public static Assertion getAssertion(final HttpSession session) {
        String attrName = "_const_cas_assertion_";
        Assertion assertion = (Assertion) (session == null ? null : session.getAttribute(attrName));
        return assertion;
    }

}
