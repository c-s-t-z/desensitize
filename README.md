# desensitize
日志脱敏 使用desensitize-service module下的jar包引入项目中 整个项目只是为了开发维护使用 

将整个项目下载到本地 然后执行 mvn 操作
使用  mvn clean package -Dmaven.test.skip=true 打包，会将整个项目打包jar

你只需要将desensitize-service模块引入项目 然后进行相关配置即可

具体配置见 [desensitize-service](desensitize-service/README.md) 