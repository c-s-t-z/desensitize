# 日志脱敏 完成部分提交

# 配置步骤

# 1

修改日志配置文件 将appender替换成 com.cstz.desensitize.logback.appender.DesensitizeConsoleAppender
如下

```
<appender name="CONSOLE" class="com.cstz.desensitize.logback.appender.DesensitizeConsoleAppender">
<encoder>
<pattern>youPathFile</pattern>
<charset>utf8</charset>
</encoder>
</appender>
```

# 2

在你需要脱敏的对象属性上增加注解 DesensitizeAnnotation 注解用于标识需要脱敏的属性
注解属性说明如下

* desensitizeHandlerClass 自定义脱敏类 多个只取第一个 如果自定义脱敏 必须指定desensitizeType属性为CUSTOM 否则不生效 枚举 DesensitizeTypeEnum

* desensitizeHandler 自定义脱敏方法 自定义脱敏类中 必须是public static 并且存在返回值 返回值必须是String 或者当前脱敏字段类型

* desensitizeType 脱敏类型枚举 默认不脱敏 DesensitizeTypeEnum.UN_DESENSITIZE

* 如果自定义类型 那么以下属性失效 需要自定义处理

> retentionPrefixLength 字符串前面需要保留的长度 默认2
> retentionSuffixLength 字符串后面需要保留长度 默认 3
> midAsteriskLength 字符串中间脱敏保留长度 默认4

# 脱敏配置文件说明

你的项目中必须配置配置文件，文件名为desensitize-logback.properties，并且放在resource目录子目录desensitize下
配置文件属性说明如下

1. desensitize.logback.enable=true 是否开启日志脱敏
2. desensitize.logback.info.handler-null-value=false
3. desensitize.logback.info.include-packages=xxx.web,xxx.config 多个使用英文逗号分开

# maven 安装依赖

使用 mvn clean package -Dmaven.test.skip=true 打包
使用mvn install:install-file -Dfile=youDir/desensitize-core-版本号.jar -DgroupId=com.cstz.logback.desensitize -DartifactId=desensitize-core -Dversion=版本号 安装到你本地的仓库中
然后在你项目中导入该依赖就可以了

# 注意事项

1. 如果脱敏对象存在循环依赖 那么脱敏后的属性值 会显示 $\_$loop$\_$
2. 如果设置的脱敏字段长度不合理 会默认保留 前一个字符串 后面是***

# 版本说明

## 1.0.0

> 项目初始化 增加脱敏功能

## 1.0.1

> 增加Marker过滤能力 通过在appender中配置filter 处理相关日志过滤   
> 配置步骤

1. 在项目配置文件desensitize-logback.properties中配置相关的Marker标记
    - desensitize.logback.info.exclude-markers 配置需要排除的Marker标记 多个使用英文逗号分开
    - desensitize.logback.info.include-markers 配置需要输出的Marker标记 多个使用英文逗号分开
2. 在logback.xml的appender下配置一个filter 如下
   ```
   <appender name="APPLICATION_WUXIAO"
   class="ch.qos.logback.core.rolling.RollingFileAppender">
   <filter class="com.cstz.desensitize.logback.filter.IncludeMarkerFilter"/>
   <file>${LOG_FILE_WUXIAO}</file>
   <encoder>
   <pattern>${FILE_LOG_PATTERN}</pattern>
   </encoder>
   <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
   <fileNamePattern>${LOG_FILE_WUXIAO}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
   <maxHistory>7</maxHistory>
   <maxFileSize>50MB</maxFileSize>
   <totalSizeCap>100MB</totalSizeCap>
   </rollingPolicy>
   </appender>
   ```
3. com.cstz.desensitize.logback.filter.IncludeMarkerFilter 需要输出的Marker标记
4. com.cstz.desensitize.logback.filter.ExcludeMarkerFilter 需要排除的Marker标记
5. 在代码的log输出中配置Marker 如下

   ```
    //其中MarkerFactory.getMarker("WUXIAO")会返回一个Marker  这个最好定义成一个静态的
    log.info(MarkerFactory.getMarker("WUXIAO"), "这是一个测试日志 WUXIAO count {} sleep {} {}", 1, 2, 3);
   ```

### 说明

> 当然你也可以使用 原始配置 如下

 ```
  <filter class="ch.qos.logback.core.filter.EvaluatorFilter">
<evaluator class="ch.qos.logback.classic.boolex.OnMarkerEvaluator">
<marker>WUXIAO</marker>
<marker>WUXIAOONE</marker>
</evaluator>
<OnMismatch>DENY</OnMismatch>
<OnMatch>NEUTRAL</OnMatch>
</filter>


<appender name="APPLICATION_WUXIAO"
class="ch.qos.logback.core.rolling.RollingFileAppender">
<filter class="ch.qos.logback.core.filter.EvaluatorFilter">
<evaluator class="ch.qos.logback.classic.boolex.OnMarkerEvaluator">
<marker>WUXIAO</marker>
<marker>WUXIAOONE</marker>
</evaluator>
<OnMismatch>DENY</OnMismatch>
<OnMatch>NEUTRAL</OnMatch>
</filter>
<file>${LOG_FILE_WUXIAO}</file>
<encoder>
<pattern>${FILE_LOG_PATTERN}</pattern>
</encoder>
<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
<fileNamePattern>${LOG_FILE_WUXIAO}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
<maxHistory>7</maxHistory>
<maxFileSize>50MB</maxFileSize>
<totalSizeCap>100MB</totalSizeCap>
</rollingPolicy>

</appender>
  ```