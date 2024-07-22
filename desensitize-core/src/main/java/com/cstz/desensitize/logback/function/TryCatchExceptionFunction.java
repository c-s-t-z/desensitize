package com.cstz.desensitize.logback.function;

import java.lang.reflect.Field;

/**
 * @Author hhsj
 * @Title: TryCatchExceptionFunction
 * @Description:
 * @Date 2024/7/19 12:10
 */
@FunctionalInterface
public interface TryCatchExceptionFunction<E extends Throwable, R> {
    R apply() throws Throwable;

    static <E extends Throwable, R> R handler(Field field, TryCatchExceptionFunction<E, R> function) {
        try {
            if (!field.isAccessible()){
                field.setAccessible(true);
            }
            return function.apply();
        } catch (Throwable e) {
            throw new RuntimeException("");
        }
    }
}
