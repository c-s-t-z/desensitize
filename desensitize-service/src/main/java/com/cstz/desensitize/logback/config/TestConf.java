package com.cstz.desensitize.logback.config;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @Author hhsj
 * @Title: TestConf
 * @Description:
 * @Date 2024/7/18 19:50
 */
@Data
@Accessors(chain = true)
public class TestConf {
    private Long id;
    private BigDecimal qian;
    private String email;
}
