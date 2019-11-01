package com.learn.client.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import(CustomClientConfiguration.class)
public @interface EnableCustomCasClient {
}
