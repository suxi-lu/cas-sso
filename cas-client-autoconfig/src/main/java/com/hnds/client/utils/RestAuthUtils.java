package com.hnds.client.utils;

import org.jasig.cas.client.util.URIBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/18 0018 15:46
 */
public class RestAuthUtils {

    private static RestAuthTgtHandler restAuthTgtHandler;

    // 需要手动或系统启动时进行初始化，默认为配置文件中的 server-url-prefix
    private static String serverUrlPrefix = "";
    // 需要手动或系统启动时进行初始化，默认为配置文件中的 server-login-url
    private static String serverLoginUrl = "";
    // 需要手动或系统启动时进行初始化，默认为配置文件中的 client-host-url
    private static String service = "";

    public static void setRestAuthTgtHandler(RestAuthTgtHandler restAuthTgtHandler) {
        RestAuthUtils.restAuthTgtHandler = restAuthTgtHandler;
    }

    static void setServerUrlPrefix(String serverUrlPrefix) {
        RestAuthUtils.serverUrlPrefix = serverUrlPrefix;
    }

    static void setService(String service) {
        RestAuthUtils.service = service;
    }

    static void setServerLoginUrl(String serverLoginUrl) {
        RestAuthUtils.serverLoginUrl = serverLoginUrl;
    }

    public static String getServerUrlPrefix() {
        return serverUrlPrefix;
    }

    public static String getServerLoginUrl() {
        return serverLoginUrl;
    }

    public static String getService() {
        return service;
    }

//    public static validUrl(String service) {
//        if (service.indexOf("?") < 0 && service.lastIndexOf("/") != service.length() - 1) {
//            str = service + "/";
//        }
//        return service.indexOf("?") < 0 && service.lastIndexOf("/") != service.length() - 1;
//    }

    /**
     * 创建tgt票据
     * @param restTemplate
     * @param username
     * @param password
     * @param service
     * @return
     */
    public static ResponseEntity<String> createTgt(RestTemplate restTemplate, String username, String password, String service) {
        LinkedMultiValueMap body = createTgtRequestBody(username, password, service);
        ResponseEntity<String> tgtResult = postCasToServer(restTemplate, serverUrlPrefix + "/v1/tickets", body);
        storeTgt(tgtResult);
        return tgtResult;
    }

    /**
     * 删除tgt票据（登出）
     * @param restTemplate
     * @param tgt
     */
    public static void deleteTgt(RestTemplate restTemplate, String tgt) {
        restTemplate.delete(serverUrlPrefix + "/v1/tickets/" + tgt);
    }

    /**
     * 存储tgt到会话id
     * @param tgtResult
     */
    public static void storeTgt(ResponseEntity<String> tgtResult) {
        String tgt = tgtResult.getBody();

        restAuthTgtHandler.handler(tgt);
    }

    /**
     * 创建tgt票据字符串
     * @param restTemplate
     * @param username
     * @param password
     * @param service
     * @return
     */
    public static String createTgtStr(RestTemplate restTemplate, String username, String password, String service) {
        ResponseEntity<String> tgtResult = createTgt(restTemplate, username, password, service);
        return tgtResult.getBody();
    }

    /**
     * 通过用户名密码和服务信息创建st票据
     * @param restTemplate
     * @param username
     * @param password
     * @param service
     * @return
     */
    public static ResponseEntity<String> createSt(RestTemplate restTemplate, String username, String password, String service) {
        ResponseEntity<String> tgtResult = RestAuthUtils.createTgt(restTemplate, username, password, service);

        if (tgtResult.getStatusCode() == HttpStatus.CREATED) {
            String tgt = tgtResult.getBody();

            ResponseEntity<String> stResult = RestAuthUtils.createSt(restTemplate, tgt, service);

            return stResult;
        }
        return null;
    }

    /**
     * 通过用户名密码和服务信息创建st票据
     * @param restTemplate
     * @param username
     * @param password
     * @param service
     * @return
     */
    public static String createStStr(RestTemplate restTemplate, String username, String password, String service) {
        ResponseEntity<String> stResult = createSt(restTemplate, username, password, service);
        return stResult.getBody();
    }

    /**
     * 创建st票据
     * @param restTemplate
     * @param tgt
     * @param service
     * @return
     */
    public static ResponseEntity<String> createSt(RestTemplate restTemplate, String tgt, String service) {
        LinkedMultiValueMap body = createStRequestBody(service);
        return postCasToServer(restTemplate, serverUrlPrefix + "/v1/tickets/" + tgt, body);
    }

    /**
     * 创建st票据返回字符串
     * @param restTemplate
     * @param tgt
     * @param service
     * @return
     */
    public static String createStStr(RestTemplate restTemplate, String tgt, String service) {
        ResponseEntity<String> stResult = createSt(restTemplate, tgt, service);
        return stResult.getBody();
    }

    /**
     * 创建tgt票据请求体
     * @param username
     * @param password
     * @param service
     * @return
     */
    public static LinkedMultiValueMap createTgtRequestBody(String username, String password, String service) {
        LinkedMultiValueMap body = new LinkedMultiValueMap();

        body.add("username", username);
        body.add("password", password);
        body.add("service", buildUrl(service));

        return body;
    }

    /**
     * 创建st票据请求体
     * @param service
     * @return
     */
    public static LinkedMultiValueMap createStRequestBody(String service) {
        LinkedMultiValueMap body = new LinkedMultiValueMap();

        body.add("service", buildUrl(service));

        return body;
    }

    /**
     * 校验服务和服务票据是否有效，有效返回对应用户信息(默认为xml格式)
     * @param restTemplate
     * @param service
     * @param st
     * @return
     */
    public static ResponseEntity<String> validate(RestTemplate restTemplate, String service, String st) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(serverUrlPrefix + "/p3/serviceValidate?service=" + buildUrl(service) + "&ticket=" + st, String.class);
        return responseEntity;
    }

    /**
     *  发送一个内容类型为application/x-www-form-urlencoded的post请求
     * @param restTemplate
     * @param url
     * @param body
     * @return
     */
    public static ResponseEntity<String> postCasToServer(RestTemplate restTemplate, String url, LinkedMultiValueMap body) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity requestEntity = new HttpEntity(body, requestHeaders);

        ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        return result;
    }

    /**
     * 通过UTIBuilder构建Url
     * @param url
     * @return
     */
    public static String buildUrl(String url) {
        return new URIBuilder(url).toString();
    }

    /**
     * 通过用户名密码和服务url创建服务ticket
     * @param restTemplate
     * @param username
     * @param password
     * @return
     */
    public static String getRedirectUrl(RestTemplate restTemplate, String username, String password) {
        return getRedirectUrl(restTemplate, username, password, service);
    }

    /**
     * 通过用户名密码和服务url创建服务ticket
     * @param restTemplate
     * @param username
     * @param password
     * @param service
     * @return
     */
    public static String getRedirectUrl(RestTemplate restTemplate, String username, String password, String service) {
        if (StringUtils.isEmpty(service)) {
            service = getService();
        }
        String st = createStStr(restTemplate, username, password, service);
        return getRedirectUrl(service, st);
    }

    /**
     * 通过当前请求地址，添加st票据参数
     */
    public static String getRedirectUrl(String url, String st) {
        return getRedirectUrl(url, "ticket", st);
    }

    /**
     * 通过当前请求地址，添加st票据参数，自定义参数名
     */
    public static String getRedirectUrl(String url, String paramName, String st) {
        URIBuilder builder = new URIBuilder(url).addParameter(paramName, st);
        return builder.toString();
    }

    /**
     * 重定向至当前请求地址，并添加st票据和服务信息
     */
    public static void redirectUrl(String url, String st) {
        String urlStr = getRedirectUrl(url, st);

//        System.out.println(url + " : " + st + " : " + urlStr);

        SpringWebUtils.redirectUrl(urlStr);
    }

}
