@echo off
@chcp 936 >nul

echo [信息] 使用Jar命令运行WMS服务端工程（Debug模式）。

title WCS服务端(Debug)

cd %~dp0

set JAVA_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

echo 正在启动Debug模式，监听端口：5005
echo 请确保在IDE中配置远程调试，连接到 localhost:5005

java -Dfile.encoding=UTF-8 %JAVA_OPTS% -Dloader.path=wcs-admin\target\lib,wcs-admin\target\resources -jar wcs-admin\target\wcs-admin.jar

pause