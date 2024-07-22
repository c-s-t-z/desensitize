package com.cstz.desensitize.logback.function;

import com.cstz.desensitize.logback.annotation.DesensitizeAnnotation;
import com.cstz.desensitize.logback.config.DesensitizeConfig;
import com.cstz.desensitize.logback.constant.CommonConstant;
import com.cstz.desensitize.logback.util.DesensitizeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author hhsj
 * @Title: DefaultDesensitizeFunction
 * @Description:
 * @Date 2024/7/20 07:55
 */
@Slf4j
public class DefaultDesensitizeFunction {
    private DefaultDesensitizeFunction() {

    }

    public static TypeEnumFunction desensitizeNameFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction desensitizeMailFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction desensitizeMobilePhoneFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction desensitizeFixedPhoneFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction desensitizeIdentityCardFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction desensitizeBankCardAccountFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction desensitizePassportFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction desensitizeOtherFunction() {
        return desensitizeCommonFunction();
    }

    public static TypeEnumFunction objectEmptyFunction() {
        return (f, o, m, l) -> {
            Object innerObj = f.get(o);
            if (l.add(innerObj.hashCode())) {
                Map<String, Object> desensitizeMap = new HashMap<>();
                Field[] fields = innerObj.getClass().getDeclaredFields();
                for (Field field : fields) {
                    DesensitizeUtil.handler(field, desensitizeMap, innerObj, l);
                }
                TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), desensitizeMap));
                return;
            }
            TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), CommonConstant.StringConstant.LOOP));
        };
    }

    public static TypeEnumFunction unDesensitizeFunction() {
        return (f, o, m, l) -> {
            TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), f.get(o)));
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
                if (isHandler(f, handler, method, parameterTypes)) {
                    methodTarget = method;
                    break;
                }
            }
            if (methodTarget == null) {
                TryCatchExceptionFunction.handler(f, () -> m.put(f.getName(), f.get(o)));
                return;
            }
            invokeCustomDesensitizeHandler(f, o, m, methodTarget);
        };
    }

    private static TypeEnumFunction desensitizeCommonFunction() {
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
                    String substring = StringUtils.substring(valueString, CommonConstant.NumberConstant.INT_ZERO, CommonConstant.NumberConstant.INT_ONE);
                    m.put(f.getName(), StringUtils.rightPad(substring, midLength + CommonConstant.NumberConstant.INT_ONE, CommonConstant.StringConstant.ASTERISK));
                    return;
                }
                String pre = StringUtils.substring(valueString, CommonConstant.NumberConstant.INT_ZERO, prefixLength);
                String suf = StringUtils.substring(valueString, valLen - suffixLength, valLen);
                String mid = StringUtils.repeat(CommonConstant.StringConstant.ASTERISK, midLength);
                m.put(f.getName(), StringUtils.join(pre, mid, suf));
            }
        };
    }

    private static void invokeCustomDesensitizeHandler(Field f, Object o, Map<String, Object> m, Method methodTarget) throws IllegalAccessException {
        Object val = null;
        try {
            val = f.get(o);
            if (DesensitizeConfig.getIsCustomHandlerNullValue()) {
                val = methodTarget.invoke(null, val);
            } else {
                if (val != null) {
                    val = methodTarget.invoke(null, val);
                }
            }
        } catch (InvocationTargetException e) {
            log.error("调用自定义脱敏handler异常 ", e);
        } finally {
            m.put(f.getName(), val);
        }
    }

    /**
     * 自定义方法必须是static静态的 参数必须是字段类型
     *
     * @param f              字段信息
     * @param handler        定义的方法名字
     * @param method         方法
     * @param parameterTypes 方法参数
     * @return 返回脱敏后的数据 类型必须是字段的类型或者String类型
     */
    private static boolean isHandler(Field f, String handler, Method method, Class<?>[] parameterTypes) {
        return Modifier.isStatic(method.getModifiers()) && handler.equals(method.getName()) && parameterTypes[0] == f.getType() && (method.getReturnType()
                .isAssignableFrom(String.class) || method.getReturnType()
                .isAssignableFrom(f.getClass()));
    }
}
