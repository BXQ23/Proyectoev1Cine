@echo off
cd /d "%~dp0"
echo Iniciando los dos nodos RabbitMQ de Cine Nocturno...
docker compose up -d
if errorlevel 1 (
  echo Error: verifica que Docker Desktop este abierto.
  pause
  exit /b 1
)
echo Espera a que ambos nodos esten saludables. Consola nodo 1: http://localhost:15672
pause
