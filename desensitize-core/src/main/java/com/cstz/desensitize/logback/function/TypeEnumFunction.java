package com.cstz.desensitize.logback.function;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

/**
 * @Author hhsj
 * @Title: TypeEnumFunction
 * @Description:
 * @Date 2024/7/19 11:27
 */
@FunctionalInterface
public interface TypeEnumFunction {
    void apply(Field field, Object obj, Map<String, Object> map, Set<Integer> loop) throws IllegalAccessException;
}
