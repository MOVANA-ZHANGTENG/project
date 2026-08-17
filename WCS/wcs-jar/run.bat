@echo off
@chcp 65001 >nul
echo.

echo [信息] 使用Jar命令运行WMS服务端工程。

echo.

title WCS服务端

cd %~dp0

set JAVA_OPTS=-Xms256m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m

set LOG_FILE=logs\wcs-admin_%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%.log
set LOG_FILE=%LOG_FILE: =0%

if not exist logs mkdir logs

echo [信息] 日志文件: %LOG_FILE%
echo [信息] 开始启动WCS服务端...
echo.

java -Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8 -jar %JAVA_OPTS% -Dloader.path=resources,lib wcs-admin.jar 2>&1 | powershell -Command "$OutputEncoding = [System.Text.Encoding]::UTF8; [Console]::OutputEncoding = [System.Text.Encoding]::UTF8; $input | ForEach-Object { Add-Content -Path '%LOG_FILE%' -Value $_ -Encoding UTF8; Write-Host $_ }"

pause
