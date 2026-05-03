@echo off
setlocal
echo [WVC] Wind V Client - Starting Build...

:: Set Java 8 Path
set "JAVA_HOME=C:\Users\kirif\Desktop\Antigravity\Java8"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo [WVC] Using Java version:
java -version

:: Run Build
call gradlew.bat build

if %ERRORLEVEL% equ 0 (
    echo.
    echo [WVC] BUILD SUCCESSFUL!
    echo Check build\libs\WindVClient-1.0.0.jar
) else (
    echo.
    echo [WVC] BUILD FAILED. Please check the errors above.
)

pause
endlocal
