@echo off
cd /d "%~dp0"
echo Esto detiene los contenedores RabbitMQ y elimina archivos de compilacion/dependencias generados.
set /p CONFIRMAR=Continuar? (S/N):
if /I not "%CONFIRMAR%"=="S" exit /b 0
docker compose down
if exist cine-backend\target rmdir /s /q cine-backend\target
if exist cine-core\target rmdir /s /q cine-core\target
if exist cine-rabbit-admin\target rmdir /s /q cine-rabbit-admin\target
if exist cine-frontend\node_modules rmdir /s /q cine-frontend\node_modules
if exist cine-frontend\dist rmdir /s /q cine-frontend\dist
set /p BORRAR_DATOS=Tambien borrar datos persistentes de RabbitMQ (volumenes)? (S/N):
if /I "%BORRAR_DATOS%"=="S" docker compose down -v
echo Listo. El codigo fuente se conserva.
pause
