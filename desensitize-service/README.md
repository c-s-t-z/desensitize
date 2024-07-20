# 日志脱敏 完成部分提交

DesensitizeAnnotation 注解用于标识需要脱敏的属性

你的项目中必须配置配置文件，文件名为desensitize-logback.properties，并且放在resource目录子目录desensitize下


# maven 安装依赖
使用  mvn clean package -Dmaven.test.skip=true 打包
使用mvn install:install-file -Dfile=youDir/desensitize-service-0.0.1-SNAPSHOT.jar  -DgroupId=com.cstz.logback.desensitize  -DartifactId=desensitize-service -Dversion=0.0.
1-SNAPSHOT安装到你本地的仓库中
