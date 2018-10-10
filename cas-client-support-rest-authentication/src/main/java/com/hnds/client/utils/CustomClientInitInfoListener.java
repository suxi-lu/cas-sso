package com.hnds.client.utils;

import com.hnds.client.session.GlobalRestTgtManager;
import com.hnds.client.utils.RestAuthUtils;
import net.unicon.cas.client.configuration.CasClientConfigurationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/21 0021 10:30
 */
public class CustomClientInitInfoListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CasClientConfigurationProperties configProps;

    @Autowired
    private GlobalRestTgtManager globalRestTgtManager;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        RestAuthUtils.setRestAuthTgtHandler(tgt -> {
            globalRestTgtManager.addTgt(SpringWebUtils.getSession().getId(), tgt);
        });

        RestAuthUtils.setServerUrlPrefix(configProps.getServerUrlPrefix());
        RestAuthUtils.setServerLoginUrl(configProps.getServerLoginUrl());
        RestAuthUtils.setService(configProps.getClientHostUrl());

        logger.debug("custom client init info success.");
    }

}
