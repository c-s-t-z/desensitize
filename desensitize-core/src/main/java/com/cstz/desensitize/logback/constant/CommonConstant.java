package com.cstz.desensitize.logback.constant;

/**
 * @Author hhsj
 * @Title: CommonConstans
 * @Description:
 * @Date 2024/7/20 07:34
 */
public interface CommonConstant {
    interface NumberConstant {
        int RETENTION_PREFIX_LENGTH = 2;
        int RETENTION_SUFFIX_LENGTH = 3;
        int MID_ASTERISK_LENGTH = 4;

        int INT_ZERO = 0;
        int INT_ONE = 1;
    }

    interface StringConstant {
        String ASTERISK = "*";
        String LOOP = "$_$loop$_$";
    }
}
