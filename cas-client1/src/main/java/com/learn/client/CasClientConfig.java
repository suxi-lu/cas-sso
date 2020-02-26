package com.learn.client;

import org.jasig.cas.client.boot.configuration.CasClientConfigurer;
import org.jasig.cas.client.boot.configuration.EnableCasClient;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCasClient
public class CasClientConfig implements CasClientConfigurer {

}
