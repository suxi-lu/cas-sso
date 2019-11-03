package com.learn.client;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.AssertionThreadLocalFilter;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class CasProtectedConfiguration {

    @Autowired
    private CasClientConfigurationProperties casClientConfigurationProperties;

    @Bean
    @ConditionalOnMissingBean(value = {SingleSignOutFilter.class})
    public FilterRegistrationBean singleSignOutFilter() {
        final SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();

        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(singleSignOutFilter);
        filterRegistrationBean.addInitParameter("casServerUrlPrefix", casClientConfigurationProperties.getServerUrlPrefix());
//        filterRegistrationBean.addUrlPatterns("/logout");
        filterRegistrationBean.setUrlPatterns(casClientConfigurationProperties.getAuthenticationUrlPatterns());
        filterRegistrationBean.setOrder(0);
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(value = {SingleSignOutHttpSessionListener.class})
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
        final SingleSignOutHttpSessionListener singleSignOutHttpSessionListener = new SingleSignOutHttpSessionListener();

        final ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listenerRegistrationBean= new ServletListenerRegistrationBean<>();
        listenerRegistrationBean.setEnabled(true);
        listenerRegistrationBean.setListener(singleSignOutHttpSessionListener);
        listenerRegistrationBean.setOrder(0);

        return listenerRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(value = {AuthenticationFilter.class})
    public FilterRegistrationBean authenticationFilter() {
        final AuthenticationFilter authenticationFilter = new AuthenticationFilter();

        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(authenticationFilter);
//        filterRegistrationBean.addInitParameter("casServerUrlPrefix", casClientConfigurationProperties.getServerUrlPrefix());
        filterRegistrationBean.setUrlPatterns(casClientConfigurationProperties.getAuthenticationUrlPatterns());
        filterRegistrationBean.setOrder(0);

        filterRegistrationBean.getInitParameters().put("casServerLoginUrl", casClientConfigurationProperties.getServerUrlPrefix());
        filterRegistrationBean.getInitParameters().put("serverName", casClientConfigurationProperties.getClientHostUrl());

        if (casClientConfigurationProperties.getGateway() != null) {
            filterRegistrationBean.getInitParameters().put("gateway", String.valueOf(casClientConfigurationProperties.getGateway()));
        }

        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean casValidationFilter() {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        
        final Cas30ProxyReceivingTicketValidationFilter cas30ProxyReceivingTicketValidationFilter = new Cas30ProxyReceivingTicketValidationFilter();

        filterRegistrationBean.setFilter(cas30ProxyReceivingTicketValidationFilter);
        filterRegistrationBean.setUrlPatterns(casClientConfigurationProperties.getValidationUrlPatterns());
        filterRegistrationBean.setOrder(1);

        filterRegistrationBean.getInitParameters().put("casServerUrlPrefix", casClientConfigurationProperties.getServerUrlPrefix());
        filterRegistrationBean.getInitParameters().put("serverName", casClientConfigurationProperties.getClientHostUrl());

        if (casClientConfigurationProperties.getUseSession() != null) {
            filterRegistrationBean.getInitParameters().put("useSession", String.valueOf(casClientConfigurationProperties.getUseSession()));
        }
        if (casClientConfigurationProperties.getRedirectAfterValidation() != null) {
            filterRegistrationBean.getInitParameters().put("redirectAfterValidation", String.valueOf(casClientConfigurationProperties.getRedirectAfterValidation()));
        }

        if (casClientConfigurationProperties.getAcceptAnyProxy() != null) {
            filterRegistrationBean.getInitParameters().put("acceptAnyProxy", String.valueOf(casClientConfigurationProperties.getAcceptAnyProxy()));
        }
        if (casClientConfigurationProperties.getAllowedProxyChains().size() > 0) {
            filterRegistrationBean.getInitParameters().put("allowedProxyChains", StringUtils.collectionToDelimitedString(casClientConfigurationProperties.getAllowedProxyChains(), " "));
        }
        if (casClientConfigurationProperties.getProxyCallbackUrl() != null) {
            filterRegistrationBean.getInitParameters().put("proxyCallbackUrl", casClientConfigurationProperties.getProxyCallbackUrl());
        }
        if (casClientConfigurationProperties.getProxyReceptorUrl() != null) {
            filterRegistrationBean.getInitParameters().put("proxyReceptorUrl", casClientConfigurationProperties.getProxyReceptorUrl());
        }

        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(value = {HttpServletRequestWrapperFilter.class})
    public FilterRegistrationBean casHttpServletRequestWrapperFilter() {
        final HttpServletRequestWrapperFilter httpServletRequestWrapperFilter = new HttpServletRequestWrapperFilter();

        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(httpServletRequestWrapperFilter);
        filterRegistrationBean.setOrder(3);

        if (casClientConfigurationProperties.getRequestWrapperUrlPatterns().size() > 0) {
            filterRegistrationBean.setUrlPatterns(casClientConfigurationProperties.getRequestWrapperUrlPatterns());
        }

        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(value = {AssertionThreadLocalFilter.class})
    public FilterRegistrationBean casAssertionThreadLocalFilter() {
        final AssertionThreadLocalFilter assertionThreadLocalFilter = new AssertionThreadLocalFilter();

        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(assertionThreadLocalFilter);
        filterRegistrationBean.setOrder(4);

        if (casClientConfigurationProperties.getAssertionThreadLocalUrlPatterns().size() > 0) {
            filterRegistrationBean.setUrlPatterns(casClientConfigurationProperties.getAssertionThreadLocalUrlPatterns());
        }

        return filterRegistrationBean;
    }

}
