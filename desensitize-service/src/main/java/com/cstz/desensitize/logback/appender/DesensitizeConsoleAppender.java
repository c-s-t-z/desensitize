package com.cstz.desensitize.logback.appender;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.cstz.desensitize.logback.util.DesensitizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author hhsj
 * @Title: DesensitizeConsoleAppender
 * @Description:
 * @Date 2024/7/18 17:17
 */
@Slf4j
public class DesensitizeConsoleAppender extends ConsoleAppender {
    @Override
    protected void subAppend(Object event) {
        try {
            if (!DesensitizeUtil.isDesensitizePackagesConfig() || !DesensitizeUtil.isEnableDesensitize()) {
                log.info("没有配置脱敏配置 没有脱敏配置信息 请检查；");
                return;
            }
            if (!(event instanceof LoggingEvent)) {
                return;
            }

            DesensitizeUtil.handlerLoggingEvent((LoggingEvent) event);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("脱敏异常", e);
        } finally {
            super.subAppend(event);
        }
    }
}
