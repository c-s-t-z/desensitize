package com.cstz.logback.desensitize.desensitize.demos.web;

import org.apache.commons.lang3.StringUtils;

/**
 * @Author hhsj
 * @Title: CustomDesensitizeHandler
 * @Description:
 * @Date 2024/7/19 13:02
 */
public class CustomDesensitizeHandler {
    public String handlerNameCustom(String name) {
        String substring = StringUtils.substring(name, 0, 3);
        String substring1 = StringUtils.substring(name, 5, 6);
        return substring + " ----- " + substring1;
    }
}
