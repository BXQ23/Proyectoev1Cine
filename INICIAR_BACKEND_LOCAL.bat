@echo off
set APP_AUTH_MODE=dev
cd /d "%~dp0cine-backend"
echo Iniciando cine-backend local en puerto 8080 (modo dev, sin Firebase real).
echo NO uses APP_AUTH_MODE=dev en AWS.
mvn spring-boot:run
pause
