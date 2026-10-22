@echo off
set RABBIT_ADMIN_API_KEY=local-admin-key-change-me
cd /d "%~dp0cine-rabbit-admin"
echo Iniciando cine-rabbit-admin en puerto 8082.
echo La API key es solo de desarrollo; no expongas este servicio en Internet.
mvn spring-boot:run
pause
