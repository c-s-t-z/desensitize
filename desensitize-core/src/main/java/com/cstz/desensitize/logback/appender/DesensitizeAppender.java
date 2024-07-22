package com.cstz.desensitize.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.cstz.desensitize.logback.util.DesensitizeUtil;

/**
 * @Author hhsj
 * @Title: AbstractDesensitizeAppender
 * @Description:
 * @Date 2024/7/22 10:42
 */
public interface DesensitizeAppender {
    default void baseSubAppender(Object event) {
        if (!DesensitizeUtil.isDesensitizePackagesConfig() || !DesensitizeUtil.isEnableDesensitize()) {
            throw new RuntimeException("没有配置脱敏配置 没有脱敏配置信息 请检查；项目resource/desensitize/desensitize-logback.properties");
        }
        if (!(event instanceof LoggingEvent)) {
            throw new RuntimeException("不支持的日志框架");
        }
        try {
            DesensitizeUtil.handlerLoggingEvent((LoggingEvent) event);
        } catch (Exception e) {
            throw new RuntimeException("脱敏异常", e);
        }
    }
}
