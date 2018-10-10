package com.hnds.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Author: luxq
 * @Description:
 * @Date: Created in 2018/5/27 0027 12:08
 */
@SpringBootApplication
//@ComponentScan("com.hnds")
//@EntityScan("com.hnds.**.domain")
public class CasAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CasAppApplication.class, args);
    }

}
