package com.cstz.desensitize.logback.appender;

import ch.qos.logback.core.rolling.RollingFileAppender;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author hhsj
 * @Title: DesensitizeRollingFileAppender
 * @Description:
 * @Date 2024/7/22 10:40
 */
@Slf4j
public class DesensitizeRollingFileAppender extends RollingFileAppender implements DesensitizeAppender {
    @Override
    protected void subAppend(Object event) {
        try {
            baseSubAppender(event);
        } catch (Exception e) {
            log.error("脱敏异常", e);
        } finally {
            super.subAppend(event);
        }
    }
}
