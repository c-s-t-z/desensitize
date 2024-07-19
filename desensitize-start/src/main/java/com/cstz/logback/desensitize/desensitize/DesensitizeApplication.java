package com.cstz.logback.desensitize.desensitize;

import com.cstz.desensitize.logback.annotation.EnableLogbackDesensitize;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = {"com.cstz.logback.desensitize", "com.cstz.desensitize.logback"})
@EnableLogbackDesensitize
//@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(value = {"com.cstz.logback.desensitize.desensitize", "com.cstz.desensitize.logback"})
public class DesensitizeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DesensitizeApplication.class, args);
    }

}
