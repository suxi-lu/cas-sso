package com.hnds.client.config;

import com.hnds.client.rest.CustomRestCasClientConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/9/21 0021 12:49
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(CustomRestConfiguration.class)
@EnableConfigurationProperties(CustomRestCasClientConfigurationProperties.class)
public @interface EnableCustomRestAuthentication {
}
