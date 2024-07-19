package com.cstz.desensitize.logback.util;

import ch.qos.logback.classic.spi.LoggingEvent;
import com.cstz.desensitize.logback.annotation.DesensitizeAnnotation;
import com.cstz.desensitize.logback.config.DesensitizeConfig;
import com.cstz.desensitize.logback.enums.DesensitizeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @Author hhsj
 * @Title: util
 * @Description:
 * @Date 2024/7/18 18:22
 */
@Slf4j
public class DesensitizeUtil {
    private DesensitizeUtil() {

    }

    public static boolean isDesensitizeField(Field field) {
        if (Objects.isNull(field)) {
            return false;
        }
        return Objects.nonNull(field.getAnnotation(DesensitizeAnnotation.class));
    }

    public static boolean isCustomDesensitizeField(Field field) {
        if (!isDesensitizeField(field)) {
            return false;
        }
        DesensitizeAnnotation desensitizeMetaInformation = field.getAnnotation(DesensitizeAnnotation.class);
        return desensitizeMetaInformation.desensitizeType() == DesensitizeTypeEnum.CUSTOM;
    }

    public static void handlerLoggingEvent(LoggingEvent event) {
        Object[] argumentArray = event.getArgumentArray();
        if (argumentArray == null || argumentArray.length == 0) {
            return;
        }
        Set<Integer> loop = new HashSet<>();
        for (int i = 0, len = argumentArray.length; i < len; i++) {
            Object param = argumentArray[i];
            if (param == null) {
                continue;
            }
            Map<String, Object> desensitizeMap = new HashMap<>();
            Object argObj = param;
            Class<?> argObjClass = argObj.getClass();
            String packageName = argObjClass.getPackage().getName();
            if (!StringUtils.startsWithAny(packageName, DesensitizeConfig.getIncludePackages().toArray(new String[0]))) {
                continue;
            }
            Field[] fields = argObjClass.getDeclaredFields();
            loop.add(argObjClass.hashCode());
            for (int j = 0, fLen = fields.length; j < fLen; j++) {
                Field field = fields[j];
                handler(field, desensitizeMap, argObj, loop);
            }
            argumentArray[i] = desensitizeMap;
        }


    }

    public static void handler(Field field, Map<String, Object> map, Object argObj, Set<Integer> loop) {
        field.setAccessible(true);
        DesensitizeAnnotation annotation = getFieldDesensitizeAnnotation(field);
        try {
            if (annotation == null) {
                DesensitizeTypeEnum.UN_DESENSITIZE.getFunction().apply(field, argObj, map, loop);
                return;
            }
            if (annotation.desensitizeType() == DesensitizeTypeEnum.NAME) {
                DesensitizeTypeEnum.NAME.getFunction().apply(field, argObj, map, loop);
                return;
            }
            if (annotation.desensitizeType() == DesensitizeTypeEnum.UN_DESENSITIZE) {
                DesensitizeTypeEnum.UN_DESENSITIZE.getFunction().apply(field, argObj, map, loop);
                return;
            }

            if (annotation.desensitizeType() == DesensitizeTypeEnum.OBJECT_DESENSITIZE) {
                DesensitizeTypeEnum.OBJECT_DESENSITIZE.getFunction().apply(field, argObj, map, loop);
                return;
            }
            if (annotation.desensitizeType() == DesensitizeTypeEnum.CUSTOM) {
                DesensitizeTypeEnum.CUSTOM.getFunction().apply(field, argObj, map, loop);
            }
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
    }

    public static boolean isDesensitizePackagesConfig() {

        return !isEmptyDesensitizePackagesConfig();
    }

    public static boolean isEmptyDesensitizePackagesConfig() {
        return DesensitizeConfig.getIncludePackages() == null || DesensitizeConfig.getIncludePackages().isEmpty();

    }

    public static boolean isEnableDesensitize() {
        return DesensitizeConfig.getEnableDesensitize();

    }

    public static DesensitizeAnnotation getFieldDesensitizeAnnotation(Field field) {
        return field.getAnnotation(DesensitizeAnnotation.class);
    }
}
//
//@Slf4j
//public class DesensitiveUtil {
//
//    /**
//     * 脱敏处理
//     */
//    public static void operation(LoggingEvent event) throws IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
//        //  获取log参数，替换占位符{}的入参
//        Object[] args =  event.getArgumentArray();
//        if(!Objects.isNull(args)){
//            for(int i =0; i<args.length; i++){
//                Object arg = args[i];
//                args[i] = toArgMap(arg);
//            }
//        }
//        event.setArgumentArray(args);
//    }
//
//    /**
//     * 将参数格式化，非自己工程的包不处理，自己的对象递归处理带注解的属性，进行格式化，替换原来的参数。
//     */
//    private static Object toArgMap(Object arg) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
//        // 通过反射的获取到参数对象
//        Class argClass = arg.getClass();
//        Package classPath = argClass.getPackage();
//        // 不是目标包不处理
//        if (!Objects.isNull(classPath) && classPath.getName().startsWith(DesensitizeConfig.BASE_PACKAGE)){
//            Map<String,Object> entityMap = new HashMap<>();
//            // 获取字段
//            entityMap = loop(arg);
//            return entityMap;
//        }
//        return arg;
//    }
//
//    /**
//     * 递归处理自有类属性，对象相互引用的情况暂时未处理，会抛出堆栈异常。
//     */
//    private static Map<String,Object> loop(Object arg) throws IllegalAccessException, NoSuchMethodException, InstantiationException, InvocationTargetException {
//        Class argClass = arg.getClass();
//        Map<String,Object> entityMap = new HashMap<>();
//        Field[] fields = argClass.getDeclaredFields();
//        if(!Objects.isNull(fields)){
//            for (int k = 0; k < fields.length; k++) {
//                Field field = fields[k];
//                field.setAccessible(true);
//                //Class fieldClass =  field.getDeclaringClass();
//                Class fieldTypeClass =  field.getType();
//                Package classPath = fieldTypeClass.getPackage();
//                Object fieldValue = field.get(arg);
//                if(Objects.isNull(classPath)){
//                    // 基本数据类型，int，long这些
//                    entityMap.put(field.getName(),fieldValue);
//                    continue;
//                }
//                String fieldClassPath = classPath.getName();
//                if(Objects.isNull(fieldValue)){
//                    // 空值字段，如果确实有对象相互依赖的，一定要处理(a1==>b==>a1  调整成  a1==>b==>a2),这样a2里的b就是空的。
//                    entityMap.put(field.getName(),fieldValue);
//                    continue;
//                }
//                if(fieldClassPath.startsWith(DesensitizeConfig.BASE_PACKAGE)){
//                    Map<String,Object> loopEntity = loop(fieldValue);
//                    entityMap.put(field.getName(),loopEntity);
//                } else {
//                    // 判断属性是否带注解
//                    Desensitize desensitizeAnnotation =  field.getAnnotation(Desensitize.class);
//                    if(Objects.isNull(desensitizeAnnotation)) {
//                        // 不脱敏的保存原来值
//                        entityMap.put(field.getName(),fieldValue);
//                    }else {
//                        // 带注解的进行脱敏
//                        DesensitizeTypeEnum desensitizeTypeEnum = desensitizeAnnotation.type();
//                        int length = desensitizeAnnotation.length() < 0 ? desensitizeTypeEnum.getLength() : desensitizeAnnotation.length();
//                        switch (desensitizeTypeEnum){
//                            case EMAIL:
//                                if(isStr(field)){
//                                    String val = desensitizeEmail(fieldValue,length);
//                                    entityMap.put(field.getName(),val);
//                                }else {
//                                    log.error("{} is need String field!",desensitizeTypeEnum.getDataType());
//                                    // 不脱敏的保存原来值
//                                    entityMap.put(field.getName(),fieldValue);
//                                }
//                                break;
//                            case PHNOE:
//                                if(isStr(field)){
//                                    String val = desensitizePhone(fieldValue,length);
//                                    entityMap.put(field.getName(),val);
//                                }else {
//                                    log.error("{} is need String field!",desensitizeTypeEnum.getDataType());
//                                    // 不脱敏的保存原来值
//                                    entityMap.put(field.getName(),fieldValue);
//                                }
//                                break;
//                            case USERNAME:
//                                if(isStr(field)){
//                                    String val = desensitizeUserName(fieldValue,length);
//                                    entityMap.put(field.getName(),val);
//                                }else {
//                                    log.error("{} is need String field!",desensitizeTypeEnum.getDataType());
//                                    // 不脱敏的保存原来值
//                                    entityMap.put(field.getName(),fieldValue);
//                                }
//                                break;
//                            case PASSWORD:
//                                // 密码全脱敏
//                                entityMap.put(field.getName(),"****");
//                                break;
//                            case ACCOUNTNUMBER:
//                                if(isStr(field)){
//                                    String val = desensitizeAccountNumber(fieldValue,length);
//                                    entityMap.put(field.getName(),val);
//                                }else {
//                                    log.error("{} is need String field!",desensitizeTypeEnum.getDataType());
//                                    // 不脱敏的保存原来值
//                                    entityMap.put(field.getName(),fieldValue);
//                                }
//                                break;
//                            case PRICE:
//                                // 价格的全脱敏
//                                entityMap.put(field.getName(),"****");
//                                break;
//                            case CUSTOM:
//                                entityMap.put(field.getName(),fieldValue);
//                                break;
//                            case COLLECTION:
//                                // 对集合类型的字段，需要判断集合里存的是什么对象
//                                entityMap.put(field.getName(),desensitizeCollect(field,fieldValue));
//                                break;
//                            default:
//                                entityMap.put(field.getName(),fieldValue);
//                        }
//                    }
//                }
//            }
//        }
//        return entityMap;
//    }
//
//    /**
//     * 脱敏嵌套的集合类型
//     * 支持：Collection，Map
//     */
//    private static Object desensitizeCollect(Field field, Object fieldValue) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException,
//    InstantiationException {
//        if(fieldValue instanceof Collection){
//            Collection coll = (Collection) fieldValue;
//            Iterator iterator = coll.iterator();
//            Collection desColl = (Collection)fieldValue.getClass().newInstance();
//            desColl.clear();
//            while(iterator.hasNext()){
//                Object item = iterator.next();
//                Object desensitizeItem = toArgMap(item);
//                desColl.add(desensitizeItem);
//            }
//            return desColl;
//        }else if(fieldValue instanceof Map){
//            // Map 字段
//            Map<Object,Object> objectMap = (Map)fieldValue;
//            Map<Object,Object> desMap = (Map)fieldValue.getClass().newInstance();
//            desMap.clear();
//            for(Object key : objectMap.keySet()){
//                Object deObject = toArgMap(objectMap.get(key));
//                desMap.put(key,deObject);
//            }
//            return desMap;
//        }else {
//            log.error("{} is not support to desensitize!",field.getType());
//        }
//        return fieldValue;
//    }
//
//    /**
//     * 脱敏账号
//     */
//    private static String desensitizeAccountNumber(Object fieldValue,int length) {
//        String account = String.valueOf(fieldValue);
//        // 邮箱地址取前半部分
//        int accountLength = account.length();
//        // 取开始脱敏的位置
//        int index = accountLength/length;
//        // 如果有邮箱地址特别短的
//        int finalLength = accountLength<length?accountLength:length;
//        // 替换字符
//        char[] chars = account.toCharArray();
//        for(int i =index;i<index+finalLength;i++){
//            chars[i] = '*';
//        }
//        return String.valueOf(chars);
//    }
//
//
//    /**
//     * 脱敏用户名
//     */
//    private static String desensitizeUserName(Object fieldValue,int length) {
//        String username = String.valueOf(fieldValue);
//        // 邮箱地址取前半部分
//        int usernameLength = username.length();
//        // 取开始脱敏的位置
//        int index = usernameLength/length;
//        // 如果有邮箱地址特别短的
//        int finalLength = usernameLength<length?usernameLength:length;
//        // 替换字符
//        char[] chars = username.toCharArray();
//        for(int i =index;i<index+finalLength;i++){
//            chars[i] = '*';
//        }
//        return String.valueOf(chars);
//    }
//
//    /**
//     * 脱敏手机号码
//     */
//    private static String desensitizePhone(Object fieldValue,int length) {
//        String phone = String.valueOf(fieldValue);
//        // 邮箱地址取前半部分
//        int phoneLength = phone.length();
//        // 取开始脱敏的位置
//        int index = phoneLength/length;
//        // 如果有邮箱地址特别短的
//        int finalLength = phoneLength<length?phoneLength:length;
//        // 替换字符
//        char[] chars = phone.toCharArray();
//        for(int i =index;i<index+finalLength;i++){
//            chars[i] = '*';
//        }
//        return String.valueOf(chars);
//    }
//
//    /**
//     * 脱敏邮件
//     */
//    private static String desensitizeEmail(Object fieldValue,int length) {
//        String email = String.valueOf(fieldValue);
//        String[] emailName = email.split("@");
//        // 邮箱地址取前半部分
//        int emailNameLength = emailName[0].length();
//        // 取开始脱敏的位置
//        int index = emailNameLength/length;
//        // 如果有邮箱地址特别短的
//        int finalLength = emailNameLength<length?emailNameLength:length;
//        // 替换字符
//        char[] chars = email.toCharArray();
//        for(int i =index;i<index+finalLength;i++){
//            chars[i] = '*';
//        }
//        return String.valueOf(chars);
//    }
//
//    /**
//     * 判断字段是否是String类型
//     */
//    private static boolean isStr(Field field){
//        return field.getType().equals(String.class);
//    }
//}

//