package com.cstz.logback.desensitize.desensitize;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = {"com.cstz.logback.desensitize", "com.cstz.desensitize.logback"})
@ComponentScan(value = {"com.cstz.logback.desensitize.desensitize", "com.cstz.desensitize.logback"})
public class DesensitizeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesensitizeApplication.class, args);
    }

}
