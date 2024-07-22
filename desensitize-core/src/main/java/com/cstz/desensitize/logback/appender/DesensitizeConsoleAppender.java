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
public class DesensitizeConsoleAppender extends ConsoleAppender implements DesensitizeAppender {
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
