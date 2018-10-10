package com.hnds.client.rest;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/21 0021 14:16
 */
@ConfigurationProperties(prefix = "cas-rest")
public class CustomRestCasClientConfigurationProperties {

    private String clientRestLoginUrl;

    public String getClientRestLoginUrl() {
        return clientRestLoginUrl;
    }

    public void setClientRestLoginUrl(String clientRestLoginUrl) {
        this.clientRestLoginUrl = clientRestLoginUrl;
    }

}
