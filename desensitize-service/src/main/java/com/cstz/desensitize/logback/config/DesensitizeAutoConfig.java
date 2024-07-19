package com.cstz.desensitize.logback.config;

import com.cstz.desensitize.logback.annotation.EnableLogbackDesensitize;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**
 * @Author hhsj
 * @Title: DesensitizeAutoConfig
 * @Description:
 * @Date 2024/7/19 17:03
 */
//@Data
//@Component
//@ConfigurationProperties("desensitize.logback")
public class DesensitizeAutoConfig {

    private boolean enable;

    private DesensitizeAutoConfigInfo desensitizeAutoConfigInfo;

}
