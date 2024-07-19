package com.cstz.desensitize.logback.enums;

import com.cstz.desensitize.logback.annotation.DesensitizeAnnotation;
import com.cstz.desensitize.logback.function.TryCatchExceptionFunction;
import com.cstz.desensitize.logback.function.TypeEnumFunction;
import com.cstz.desensitize.logback.util.DesensitizeUtil;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    CUSTOM(0, "自定义脱敏", customEmptyFunction()),

    NAME(1, "姓名", desensitizeNameFunction()),
    /**
     * 字段显示为****
     */
    OBJECT_DESENSITIZE(-1, "对象脱敏", objectEmptyFunction()),
    UN_DESENSITIZE(-2, "不脱敏", unDesensitizeFunction()),
    ;

    private static TypeEnumFunction desensitizeNameFunction() {
        return (f, o, m, l) -> {
            DesensitizeAnnotation annotation = f.getAnnotation(DesensitizeAnnotation.class);
            int prefixLength = annotation.retentionPrefixLength();
            int suffixLength = annotation.retentionSuffixLength();
            int midLength = annotation.midAsteriskLength();
            Object value = TryCatchExceptionFunction.handler(f, () -> f.get(o));
            if (value instanceof String) {
                String valueString = (String) value;
                int valLen = valueString.length();
                if (valLen <= prefixLength + suffixLength || valLen <= suffixLength || prefixLength > valLen) {
                    String substring = StringUtils.substring(valueString, 0, 1);
                    m.put(f.getName(), StringUtils.rightPad(substring, midLength + 1, "*"));
                    return;
                }
                String pre = StringUtils.substring(valueString, 0, prefixLength);
                String suf = StringUtils.substring(valueString, valLen - suffixLength, valLen);
                String mid = StringUtils.repeat("*", midLength);
                m.put(f.getName(), StringUtils.join(pre, mid, suf));
            }
        };
    }

    public static TypeEnumFunction objectEmptyFunction() {
        return (f, o, m, l) -> {
            Object innerObj = f.get(o);
            if (l.add(innerObj.hashCode())) {
                Map<String, Object> desensitizeMap = new HashMap<>();
                Field[] fields = innerObj.getClass().getDeclaredFields();
                for (int j = 0, fLen = fields.length; j < fLen; j++) {
                    Field field = fields[j];
                    DesensitizeUtil.handler(field, desensitizeMap, innerObj, l);
                }
                TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), desensitizeMap));
            }
        };
    }

    public static TypeEnumFunction customEmptyFunction() {
        return (f, o, m, l) -> {
            DesensitizeAnnotation annotation = f.getAnnotation(DesensitizeAnnotation.class);
            Class<?>[] handlerClass = annotation.desensitizeHandlerClass();
            String handler = annotation.desensitizeHandler();
            if (handlerClass == null || handlerClass.length == 0 || StringUtils.isBlank(handler)) {
                TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), f.get(o)));
                return;
            }
            Class<?> handlerClassTarget = handlerClass[0];
            Method[] methods = handlerClassTarget.getMethods();
            Method methodTarget = null;
            for (Method method : methods) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 0) {
                    TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), f.get(o)));
                    return;
                }
                if (handler.equals(method.getName()) && parameterTypes[0] == f.getType() && method.getReturnType().isAssignableFrom(String.class)) {
                    methodTarget = method;
                    break;
                }
            }
            if (methodTarget == null) {
                TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), f.get(o)));
                return;
            }
            Object val = null;
            try {
                val = f.get(o);
                if (val != null) {
                    val = methodTarget.invoke(null, val);
                }
            } catch (InvocationTargetException e) {
                throw new RuntimeException();
            } finally {
                m.put(f.getName(), val);
            }
        };
    }

    public static TypeEnumFunction unDesensitizeFunction() {
        return (f, o, m, l) -> {
            TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), f.get(o)));
        };
    }

    private int code;

    private String desc;

    private TypeEnumFunction function;

    DesensitizeTypeEnum(int code, String desc, TypeEnumFunction function) {
        this.code = code;
        this.desc = desc;
        this.function = function;
    }

    public void handlerEnum(DesensitizeTypeEnum typeEnum) {

    }
}
