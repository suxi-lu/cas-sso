package com.hnds.client.config;

import com.hnds.client.session.DefaultGlobalSessionManager;
import com.hnds.client.session.GlobalSessionManager;
import com.hnds.client.session.GlobalUserHandler;
import com.hnds.client.utils.CustomClientFilter;
import net.unicon.cas.client.configuration.CasClientConfigurationProperties;
import net.unicon.cas.client.configuration.EnableCasClient;
import org.jasig.cas.client.session.CustomBaseClientFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableCasClient
@EnableConfigurationProperties(CasClientConfigurationProperties.class)
public class CustomClientConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
    private static SingleSignOutHttpSessionListener singleSignOutHttpSessionListener = new SingleSignOutHttpSessionListener();

    @Autowired
    private CasClientConfigurationProperties configProps;

    @Autowired(required = false)
    private GlobalSessionManager globalSessionManager;
    @Autowired(required = false)
    private GlobalUserHandler globalUserHandler;

    @Bean
    @ConditionalOnMissingBean(value = {RestTemplate.class})
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @ConditionalOnMissingBean(value = {GlobalSessionManager.class})
    public GlobalSessionManager globalSessionManager() {
        return new DefaultGlobalSessionManager();
    }

    @Bean
    @ConditionalOnMissingBean(value = {SingleSignOutFilter.class})
//    @ConditionalOnMissingBean(value = {CustomBaseClientFilter.class})
    public FilterRegistrationBean singleSignOutFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(singleSignOutFilter);
        filterRegistrationBean.addInitParameter("casServerUrlPrefix", configProps.getServerUrlPrefix());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(0);
        logger.debug("init `SingleSignOutFilter`.");
        return filterRegistrationBean;
    }

    @Bean
    @ConditionalOnMissingBean(value = {SingleSignOutHttpSessionListener.class})
//    @ConditionalOnMissingBean(value = {CustomBaseClientFilter.class})
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener(){
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listenerRegistrationBean= new ServletListenerRegistrationBean<>();
        listenerRegistrationBean.setEnabled(true);
        listenerRegistrationBean.setListener(singleSignOutHttpSessionListener);
        listenerRegistrationBean.setOrder(0);
        logger.debug("init `SingleSignOutHttpSessionListener`.");
        return listenerRegistrationBean;
    }

    @Bean
    @ConditionalOnBean(value = {GlobalSessionManager.class})
    public FilterRegistrationBean customClientFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new CustomClientFilter(globalSessionManager, globalUserHandler));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(0);
        logger.debug("init `CustomBaseClientFilter`.");
        return filterRegistrationBean;
    }

}
