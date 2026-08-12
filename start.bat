@echo off
setlocal EnableExtensions EnableDelayedExpansion

set "APP_HOME=%~dp0"
set "JVM_OPTS=-Xms512m -Xmx1024m -Dfile.encoding=UTF-8"
set "LOG_DIR=%APP_HOME%logs"
set "SQL_DIR=%APP_HOME%sql\startup"
set "SQL_DONE_DIR=%SQL_DIR%\.done"
set "LOCAL_ENV_FILE=%APP_HOME%start.local.bat"

if exist "%LOCAL_ENV_FILE%" call "%LOCAL_ENV_FILE%"

if not defined MYSQL_CMD (
    for /f "delims=" %%I in ('where mysql 2^>nul') do (
        if not defined MYSQL_CMD set "MYSQL_CMD=%%I"
    )
)
if not defined DB_HOST set "DB_HOST=localhost"
if not defined DB_PORT set "DB_PORT=3308"
if not defined DB_NAME set "DB_NAME=wms"
if not defined DB_USER set "DB_USER=root"
if not defined DB_PASS set "DB_PASS=12345"

if not exist "%LOG_DIR%" mkdir "%LOG_DIR%"

echo Starting application...
echo Info: Logging to console and saving to "%LOG_DIR%" (tomLog keeps 3 days).
echo -------------------------------------------------------------------

"%MYSQL_CMD%" --version >nul 2>nul
if errorlevel 1 (
    echo ERROR: mysql client not found.
    echo Please add mysql.exe to PATH, or create "%LOCAL_ENV_FILE%" and set MYSQL_CMD there.
    echo You can copy start.local.example.bat to start.local.bat and adjust it once per server.
    pause
    exit /b 1
)

if not exist "%SQL_DIR%" mkdir "%SQL_DIR%"
if not exist "%SQL_DONE_DIR%" mkdir "%SQL_DONE_DIR%"

echo Running startup SQL scripts from "%SQL_DIR%"...
set "HAS_SQL=0"

for /f "delims=" %%F in ('dir /b /a-d "%SQL_DIR%\*.sql" 2^>nul') do (
    set "HAS_SQL=1"
    set "SCRIPT_FILE=%SQL_DIR%\%%F"
    set "MARK_FILE=%SQL_DONE_DIR%\%%~nF.done"

    if exist "!MARK_FILE!" (
        echo Skip applied startup SQL: %%F
    ) else (
        echo Running startup SQL: %%F
        set "MYSQL_PWD=%DB_PASS%"
        "%MYSQL_CMD%" --default-character-set=utf8mb4 -h%DB_HOST% -P%DB_PORT% -u%DB_USER% %DB_NAME% < "!SCRIPT_FILE!"
        set "EXIT_CODE=!ERRORLEVEL!"
        set "MYSQL_PWD="

        if not "!EXIT_CODE!"=="0" (
            echo ERROR: Startup SQL execution failed for %%F. Application not started.
            pause
            exit /b !EXIT_CODE!
        )

        > "!MARK_FILE!" echo Applied at %date% %time%
        echo Startup SQL completed: %%F
    )
)

if "!HAS_SQL!"=="0" (
    echo No startup SQL files found. Skip database patch step.
)

echo -------------------------------------------------------------------

java %JVM_OPTS% -Dloader.path=resources/ -cp wms-admin.jar org.springframework.boot.loader.PropertiesLauncher

echo.
echo -------------------------------------------------------------------
echo Application exited.

pause
