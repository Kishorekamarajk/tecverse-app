@echo off
echo ===================================================
echo Starting TEC-VERSE 2026 Local Backend API Server
echo ===================================================

set DB_HOST=10.184.48.20
set DB_PORT=5432
set DB_NAME=tecverse_db
set DB_USER=postgres
set DB_PASSWORD=NavyExam
set PORT=2303
set MAIL_USERNAME=noreply-cbt@cdac.in
set MAIL_PASSWORD=CBT-exam12345!@#$%

echo Database: %DB_NAME% on %DB_HOST%:%DB_PORT%
echo Port: %PORT%
echo.

if exist "C:\Users\KISHORE\Desktop\tec-app\backend" (
    cd /d "C:\Users\KISHORE\Desktop\tec-app\backend"
) else (
    cd /d "%~dp0..\backend"
)
mvn spring-boot:run
