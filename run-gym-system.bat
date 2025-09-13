@echo off
echo ========================================
echo    Gym Management System Launcher
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java from https://java.com
    pause
    exit /b 1
)

REM Check if the JAR file exists
if not exist "target\Gym-Management-System-0.0.1-SNAPSHOT.jar" (
    echo ERROR: JAR file not found!
    echo Expected location: target\Gym-Management-System-0.0.1-SNAPSHOT.jar
    echo.
    echo Please make sure:
    echo 1. You are running this from the project root directory
    echo 2. The application has been built with 'mvn package' or similar
    pause
    exit /b 1
)

echo Starting Gym Management System...
echo.
java -jar target\Gym-Management-System-0.0.1-SNAPSHOT.jar

REM Pause to see any error messages if the application closes
if errorlevel 1 (
    echo.
    echo Application exited with error code: %errorlevel%
    pause
) else (
    echo.
    echo Application closed successfully
    pause
)