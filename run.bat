@echo off
setlocal enabledelayedexpansion

REM Set project directory
set PROJECT_DIR=E:\java\project\BankingSystem
cd /d %PROJECT_DIR%

REM Compile the project using Maven
echo Building project...
call mvn clean compile

REM Check if Maven build was successful
if %errorlevel% neq 0 (
    echo Maven build failed, stopping script
    pause
    exit /b %errorlevel%
)

REM Set the classpath with all required dependencies
set CLASSPATH="%PROJECT_DIR%\target\classes;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar;%USERPROFILE%\.m2\repository\com\google\protobuf\protobuf-java\3.21.9\protobuf-java-3.21.9.jar"

REM Run the BankingApp
echo Starting Banking System...
java -cp %CLASSPATH% com.kushagra.BankingApp

REM Keep window open if there's an error
if %errorlevel% neq 0 (
    echo Application terminated with error code %errorlevel%
    pause
)