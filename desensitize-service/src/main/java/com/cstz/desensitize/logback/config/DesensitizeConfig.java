package com.cstz.desensitize.logback.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * @Author hhsj
 * @Title: DesensitizeConfig
 * @Description:
 * @Date 2024/7/18 18:51
 */
public class DesensitizeConfig {

    private DesensitizeConfig() {
        //throw new UnsupportedOperationException("不允许操作");
    }

    private static volatile DesensitizeConfig INSTANCE;

    private static Set<String> includePackages;
    private static boolean enableDesensitize;

    /**
     * 如果字段值是null 是否会调用自定义函数
     */
    private static boolean isCustomHandlerNullValue;

    public static Set<String> getIncludePackages() {
        loadConfigComplete();
        return includePackages;
    }

    public static boolean getEnableDesensitize() {
        loadConfigComplete();
        return enableDesensitize;
    }

    public static boolean getIsCustomHandlerNullValue() {
        loadConfigComplete();
        return isCustomHandlerNullValue;
    }

    public static void loadConfig() {
        ClassPathResource classPathResource = new ClassPathResource("/desensitize/desensitize-logback.properties");
        Properties properties = new Properties();
        try {
            properties.load(classPathResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String propertyValue = properties.getProperty("desensitize.logback.info.include-packages");
        String isCustomHandlerNull = properties.getProperty("desensitize.logback.info.handler-null-value");
        String enable = properties.getProperty("desensitize.logback.enable");
        includePackages = StringUtils.isBlank(propertyValue) ? new HashSet<>(0) :
                Arrays.stream(StringUtils.split(propertyValue, ",")).map(StringUtils::trim).collect(Collectors.toSet());
        enableDesensitize = Boolean.parseBoolean(enable);
        isCustomHandlerNullValue = Boolean.parseBoolean(isCustomHandlerNull);
    }

    private static boolean loadConfigComplete() {
        if (INSTANCE == null) {
            synchronized (DesensitizeConfig.class) {
                if (INSTANCE == null) {
                    loadConfig();
                    INSTANCE = new DesensitizeConfig();
                }
            }
        }
        return true;
    }
}
