package com.cstz.desensitize.logback.enums;

import com.cstz.desensitize.logback.annotation.DesensitizeAnnotation;
import com.cstz.desensitize.logback.config.DesensitizeConfig;
import com.cstz.desensitize.logback.constant.CommonConstant;
import com.cstz.desensitize.logback.function.DefaultDesensitizeFunction;
import com.cstz.desensitize.logback.function.TryCatchExceptionFunction;
import com.cstz.desensitize.logback.function.TypeEnumFunction;
import com.cstz.desensitize.logback.util.DesensitizeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @Author hhsj
 * @Title: DesensitizeTypeEnum
 * @Description:
 * @Date 2024/7/18 18:06
 */
@Getter
public enum DesensitizeTypeEnum {

    /**
     * 自定义脱敏规则
     */
    CUSTOM(0, "自定义脱敏", DefaultDesensitizeFunction.customEmptyFunction()),

    NAME(1, "姓名", DefaultDesensitizeFunction.desensitizeNameFunction()),
    MAIL(2, "邮箱", DefaultDesensitizeFunction.desensitizeMailFunction()),

    MOBILE_PHONE(3, "手机", DefaultDesensitizeFunction.desensitizeMobilePhoneFunction()),

    FIXED_TELEPHONE(4, "固话", DefaultDesensitizeFunction.desensitizeFixedPhoneFunction()),
    IDENTITY_CARD(5, "身份证", DefaultDesensitizeFunction.desensitizeIdentityCardFunction()),
    BANK_CARD_ACCOUNT(6, "银行卡", DefaultDesensitizeFunction.desensitizeBankCardAccountFunction()),
    PASSPORT(7, "护照", DefaultDesensitizeFunction.desensitizePassportFunction()),
    OTHER_INFORMATION(100, "其他", DefaultDesensitizeFunction.desensitizeOtherFunction()),

    /**
     * 字段显示为****
     */
    OBJECT_DESENSITIZE(-1, "对象脱敏", DefaultDesensitizeFunction.objectEmptyFunction()),
    UN_DESENSITIZE(-2, "不脱敏", DefaultDesensitizeFunction.unDesensitizeFunction()),
    ;
    private final int code;

    private final String desc;

    private final TypeEnumFunction function;

    DesensitizeTypeEnum(int code, String desc, TypeEnumFunction function) {
        this.code = code;
        this.desc = desc;
        this.function = function;
    }
}
