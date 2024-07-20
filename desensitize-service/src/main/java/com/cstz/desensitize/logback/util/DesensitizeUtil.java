package com.cstz.desensitize.logback.util;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.cstz.desensitize.logback.annotation.DesensitizeAnnotation;
import com.cstz.desensitize.logback.config.DesensitizeConfig;
import com.cstz.desensitize.logback.enums.DesensitizeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @Author hhsj
 * @Title: util
 * @Description:
 * @Date 2024/7/18 18:22
 */
@Slf4j
public class DesensitizeUtil {
    private DesensitizeUtil() {

    }

    public static boolean isDesensitizeField(Field field) {
        if (Objects.isNull(field)) {
            return false;
        }
        return Objects.nonNull(field.getAnnotation(DesensitizeAnnotation.class));
    }

    public static boolean isCustomDesensitizeField(Field field) {
        if (!isDesensitizeField(field)) {
            return false;
        }
        DesensitizeAnnotation desensitizeMetaInformation = field.getAnnotation(DesensitizeAnnotation.class);
        return desensitizeMetaInformation.desensitizeType() == DesensitizeTypeEnum.CUSTOM;
    }

    public static void handlerLoggingEvent(LoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (argumentArray == null || argumentArray.length == 0) {
            return;
        }
        Set<Integer> loop = new HashSet<>();
        for (int i = 0, len = argumentArray.length; i < len; i++) {
            Object param = argumentArray[i];
            if (param == null) {
                continue;
            }
            Map<String, Object> desensitizeMap = new HashMap<>();
            Object argObj = param;
            Class<?> argObjClass = argObj.getClass();
            String packageName = argObjClass.getPackage().getName();
            if (!StringUtils.startsWithAny(packageName, DesensitizeConfig.getIncludePackages().toArray(new String[0]))) {
                continue;
            }
            Field[] fields = argObjClass.getDeclaredFields();
            loop.add(argObjClass.hashCode());
            for (int j = 0, fLen = fields.length; j < fLen; j++) {
                Field field = fields[j];
                handler(field, desensitizeMap, argObj, loop);
            }
            argumentArray[i] = desensitizeMap;
        }
    }

    public static void handler(Field field, Map<String, Object> map, Object argObj, Set<Integer> loop) {
        field.setAccessible(true);
        DesensitizeAnnotation annotation = getFieldDesensitizeAnnotation(field);
        try {
            if (annotation == null) {
                DesensitizeTypeEnum.UN_DESENSITIZE.getFunction().apply(field, argObj, map, loop);
                return;
            }
            if (annotation.desensitizeType() == DesensitizeTypeEnum.NAME) {
                DesensitizeTypeEnum.NAME.getFunction().apply(field, argObj, map, loop);
                return;
            }
            if (annotation.desensitizeType() == DesensitizeTypeEnum.MAIL) {
                DesensitizeTypeEnum.MAIL.getFunction().apply(field, argObj, map, loop);
                return;
            }
            if (annotation.desensitizeType() == DesensitizeTypeEnum.UN_DESENSITIZE) {
                DesensitizeTypeEnum.UN_DESENSITIZE.getFunction().apply(field, argObj, map, loop);
                return;
            }

            if (annotation.desensitizeType() == DesensitizeTypeEnum.OBJECT_DESENSITIZE) {
                DesensitizeTypeEnum.OBJECT_DESENSITIZE.getFunction().apply(field, argObj, map, loop);
                return;
            }
            if (annotation.desensitizeType() == DesensitizeTypeEnum.CUSTOM) {
                DesensitizeTypeEnum.CUSTOM.getFunction().apply(field, argObj, map, loop);
            }
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
    }

    public static boolean isDesensitizePackagesConfig() {

        return !isEmptyDesensitizePackagesConfig();
    }

    public static boolean isEmptyDesensitizePackagesConfig() {
        return DesensitizeConfig.getIncludePackages() == null || DesensitizeConfig.getIncludePackages().isEmpty();

    }

    public static boolean isEnableDesensitize() {
        return DesensitizeConfig.getEnableDesensitize();

    }

    public static DesensitizeAnnotation getFieldDesensitizeAnnotation(Field field) {
        return field.getAnnotation(DesensitizeAnnotation.class);
    }
}