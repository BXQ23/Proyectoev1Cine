@echo off
cd /d "%~dp0cine-core"
echo Iniciando cine-core en puerto 8081...
mvn spring-boot:run
pause
