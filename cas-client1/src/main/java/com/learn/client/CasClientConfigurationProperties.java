package com.learn.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(value = "cas", ignoreUnknownFields = false)
public class CasClientConfigurationProperties {

    @NotNull
    private String serverUrlPrefix;
    @NotNull
    private String serverLoginUrl;
    @NotNull
    private String clientHostUrl;
    private List<String> authenticationUrlPatterns = new ArrayList<>();
    private List<String> validationUrlPatterns = new ArrayList<>();
    private List<String> requestWrapperUrlPatterns = new ArrayList<>();
    private List<String> assertionThreadLocalUrlPatterns = new ArrayList<>();
    private Boolean gateway;
    private Boolean useSession;
    private Boolean redirectAfterValidation;
    private Boolean acceptAnyProxy;
    private List<String> allowedProxyChains = new ArrayList<>();
    private String proxyCallbackUrl;
    private String proxyReceptorUrl;

    public String getServerUrlPrefix() {
        return serverUrlPrefix;
    }

    public void setServerUrlPrefix(String serverUrlPrefix) {
        this.serverUrlPrefix = serverUrlPrefix;
    }

    public String getServerLoginUrl() {
        return serverLoginUrl;
    }

    public void setServerLoginUrl(String serverLoginUrl) {
        this.serverLoginUrl = serverLoginUrl;
    }

    public String getClientHostUrl() {
        return clientHostUrl;
    }

    public void setClientHostUrl(String clientHostUrl) {
        this.clientHostUrl = clientHostUrl;
    }

    public List<String> getAuthenticationUrlPatterns() {
        return authenticationUrlPatterns;
    }

    public void setAuthenticationUrlPatterns(List<String> authenticationUrlPatterns) {
        this.authenticationUrlPatterns = authenticationUrlPatterns;
    }

    public List<String> getValidationUrlPatterns() {
        return validationUrlPatterns;
    }

    public void setValidationUrlPatterns(List<String> validationUrlPatterns) {
        this.validationUrlPatterns = validationUrlPatterns;
    }

    public List<String> getRequestWrapperUrlPatterns() {
        return requestWrapperUrlPatterns;
    }

    public void setRequestWrapperUrlPatterns(List<String> requestWrapperUrlPatterns) {
        this.requestWrapperUrlPatterns = requestWrapperUrlPatterns;
    }

    public List<String> getAssertionThreadLocalUrlPatterns() {
        return assertionThreadLocalUrlPatterns;
    }

    public void setAssertionThreadLocalUrlPatterns(List<String> assertionThreadLocalUrlPatterns) {
        this.assertionThreadLocalUrlPatterns = assertionThreadLocalUrlPatterns;
    }

    public Boolean getGateway() {
        return gateway;
    }

    public void setGateway(Boolean gateway) {
        this.gateway = gateway;
    }

    public Boolean getUseSession() {
        return useSession;
    }

    public void setUseSession(Boolean useSession) {
        this.useSession = useSession;
    }

    public Boolean getRedirectAfterValidation() {
        return redirectAfterValidation;
    }

    public void setRedirectAfterValidation(Boolean redirectAfterValidation) {
        this.redirectAfterValidation = redirectAfterValidation;
    }

    public Boolean getAcceptAnyProxy() {
        return acceptAnyProxy;
    }

    public void setAcceptAnyProxy(Boolean acceptAnyProxy) {
        this.acceptAnyProxy = acceptAnyProxy;
    }

    public List<String> getAllowedProxyChains() {
        return allowedProxyChains;
    }

    public void setAllowedProxyChains(List<String> allowedProxyChains) {
        this.allowedProxyChains = allowedProxyChains;
    }

    public String getProxyCallbackUrl() {
        return proxyCallbackUrl;
    }

    public void setProxyCallbackUrl(String proxyCallbackUrl) {
        this.proxyCallbackUrl = proxyCallbackUrl;
    }

    public String getProxyReceptorUrl() {
        return proxyReceptorUrl;
    }

    public void setProxyReceptorUrl(String proxyReceptorUrl) {
        this.proxyReceptorUrl = proxyReceptorUrl;
    }

}
