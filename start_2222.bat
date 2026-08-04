@echo off

setlocal
 
set JVM_OPTS=-Xms512m -Xmx1024m -Dfile.encoding=UTF-8

set LOG_DIR=logs
 
if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"
 
echo Starting application...

echo Info: Logging to console and saving to "%LOG_DIR%" (tomLog keeps 3 days).

echo -------------------------------------------------------------------
 
java %JVM_OPTS% -Dloader.path=resources/ -cp wms-admin.jar org.springframework.boot.loader.PropertiesLauncher
 
echo.

echo -------------------------------------------------------------------

echo Application exited.

pause
 