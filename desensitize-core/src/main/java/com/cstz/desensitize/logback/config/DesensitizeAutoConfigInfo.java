package com.cstz.desensitize.logback.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author hhsj
 * @Title: DesensitizeAutoConfigInfo
 * @Description:
 * @Date 2024/7/19 17:16
 */
//@Data
//@Component
//@ConfigurationProperties(value = "desensitize.logback.info")
public class DesensitizeAutoConfigInfo {
    private Set<String> includePackages;
}
