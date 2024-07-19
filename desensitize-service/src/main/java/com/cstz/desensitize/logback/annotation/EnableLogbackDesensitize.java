package com.cstz.desensitize.logback.annotation;

import com.cstz.desensitize.logback.config.DesensitizeAutoConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author hhsj
 * @Title: EnableLogbackDesensitize
 * @Description:
 * @Date 2024/7/18 19:08
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DesensitizeAutoConfig.class)
public @interface EnableLogbackDesensitize {
}
