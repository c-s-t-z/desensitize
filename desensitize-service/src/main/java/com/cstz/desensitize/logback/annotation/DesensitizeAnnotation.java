package com.cstz.desensitize.logback.annotation;

import com.cstz.desensitize.logback.constant.CommonConstant;
import com.cstz.desensitize.logback.enums.DesensitizeTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author hhsj
 * @Title: DesensitizeEnum
 * @Description:
 * @Date 2024/7/18 18:04
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DesensitizeAnnotation {
    /**
     * 自定义脱敏类 如果自定义脱敏 必须指定desensitizeType属性为CUSTOM 否则不生效 {@link DesensitizeTypeEnum}
     *
     * @return
     */
    Class<?>[] desensitizeHandlerClass() default {};

    /**
     * 自定义脱敏方法 必须是public static 并且存在返回值
     *
     * @return
     */
    String desensitizeHandler() default "";

    /**
     * 内置脱敏类型枚举
     *
     * @return {@link DesensitizeTypeEnum}
     */
    DesensitizeTypeEnum desensitizeType() default DesensitizeTypeEnum.UN_DESENSITIZE;

    /**
     * 如果自定义类型 那么以下属性失效 需要自定义处理
     *
     * @return
     */
    int retentionPrefixLength() default CommonConstant.NumberConstant.RETENTION_PREFIX_LENGTH;

    int retentionSuffixLength() default CommonConstant.NumberConstant.RETENTION_SUFFIX_LENGTH;

    int midAsteriskLength() default CommonConstant.NumberConstant.MID_ASTERISK_LENGTH;

}
