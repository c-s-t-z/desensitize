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

    private static Set<String> includeMarkers;

    private static Set<String> excludeMarkers;

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

    public static Set<String> getIncludeMarkers() {
        loadConfigComplete();
        return includeMarkers;
    }

    public static Set<String> getExcludeMarkers() {
        loadConfigComplete();
        return excludeMarkers;
    }

    public static void loadConfig() {
        Properties desensitizeProperties = getDesensitizeProperties();
        loadIncludePackages(desensitizeProperties);
        loadEnable(desensitizeProperties);
        loadIsCustomHandlerNullValue(desensitizeProperties);
        loadIncludeMarkers(desensitizeProperties);
        loadExcludeMarkers(desensitizeProperties);
    }

    private static void loadExcludeMarkers(Properties desensitizeProperties) {
        String excludeMarker = desensitizeProperties.getProperty("desensitize.logback.info.exclude-markers");
        excludeMarkers = StringUtils.isBlank(excludeMarker) ? new HashSet<>(0) :
                Arrays.stream(StringUtils.split(excludeMarker, ",")).map(StringUtils::trim).collect(Collectors.toSet());
    }

    private static void loadIncludeMarkers(Properties desensitizeProperties) {
        String includeMarker = desensitizeProperties.getProperty("desensitize.logback.info.include-markers");
        includeMarkers = StringUtils.isBlank(includeMarker) ? new HashSet<>(0) :
                Arrays.stream(StringUtils.split(includeMarker, ",")).map(StringUtils::trim).collect(Collectors.toSet());
    }

    private static void loadEnable(Properties desensitizeProperties) {
        String enable = desensitizeProperties.getProperty("desensitize.logback.enable");
        enableDesensitize = Boolean.parseBoolean(enable);
    }

    private static void loadIsCustomHandlerNullValue(Properties desensitizeProperties) {
        String isCustomHandlerNull = desensitizeProperties.getProperty("desensitize.logback.info.handler-null-value");
        isCustomHandlerNullValue = Boolean.parseBoolean(isCustomHandlerNull);
    }

    private static void loadIncludePackages(Properties desensitizeProperties) {
        String propertyValue = desensitizeProperties.getProperty("desensitize.logback.info.include-packages");
        includePackages = StringUtils.isBlank(propertyValue) ? new HashSet<>(0) :
                Arrays.stream(StringUtils.split(propertyValue, ",")).map(StringUtils::trim).collect(Collectors.toSet());
    }

    private static Properties getDesensitizeProperties() {
        ClassPathResource classPathResource = new ClassPathResource("/desensitize/desensitize-logback.properties");
        Properties properties = new Properties();
        try {
            properties.load(classPathResource.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
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
