# 版本说明

## 1.0.2
> 维护配置demo 代码功能无变化

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

## 1.0.0

> 项目初始化 增加脱敏功能
