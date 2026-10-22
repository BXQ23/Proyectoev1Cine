@echo off
cd /d "%~dp0cine-frontend"
if not exist node_modules (
  echo Instalando dependencias npm...
  npm install
  if errorlevel 1 (
    echo Error al instalar dependencias.
    pause
    exit /b 1
  )
)
npm run dev
pause
