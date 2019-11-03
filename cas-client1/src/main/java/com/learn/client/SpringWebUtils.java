package com.learn.client;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author: luxq
 * @Description: 基于spring的获取上下文 request, response, session 及其属性和扩展方法
 * @Date: Created in 2018/9/18 0018 16:09
 */
public class SpringWebUtils {

    /**
     * 获取servlet请求对象request
     * @return
     */
    public static ServletRequestAttributes getServletRequest() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    /**
     * 获取请求对象
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = getServletRequest();
        return attributes.getRequest();
    }

    /**
     * 获取请求参数值
     * @return
     */
    public static String getRequestParam(String paramName) {
        HttpServletRequest request = getRequest();
        return request.getParameter(paramName);
    }

    /**
     * 获取请求属性值
     * @return
     */
    public static Object getRequestAttribute(String attrName) {
        HttpServletRequest request = getRequest();
        return request.getAttribute(attrName);
    }

    /**
     * 获取响应对象
     * @return
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = getServletRequest();
        return attributes.getResponse();
    }

    /**
     * 获取会话
     * @return
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        return request.getSession();
    }

    /**
     * 获取会话属性值
     * @return
     */
    public static Object getSessionAttribute(String attrName) {
        HttpSession session = getSession();
        return session.getAttribute(attrName);
    }

    /**
     * 设置会话属性
     * @param object
     */
    public static void setSessionAttribute(String attrName, Object object) {
        if (object == null) return;
        HttpSession session = getSession();
        session.setAttribute(attrName, object);
    }

    /**
     * 重定向地址
     * @param url
     */
    public static void redirectUrl(String url) {
        try {
            getResponse().sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
