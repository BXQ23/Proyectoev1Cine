# cine-frontend

Interfaz React + Vite para consultar cartelera, iniciar sesión y gestionar reservas.

## Desarrollo local

1. Instala Node.js y npm.
2. Copia `.env.example` como `.env`.
3. Ejecuta `npm install` y después `npm run dev`.
4. Abre `http://localhost:3000`.

El backend local debe responder en `http://localhost:8080`. Para producción, `VITE_API_BASE_URL` debe apuntar al dominio HTTPS que sirve el frontend y reenvía `/api/` a `cine-backend`.

La configuración web de Firebase está en `src/firebase.js`. El JSON privado de Firebase Admin no forma parte del frontend ni debe guardarse en el repositorio.

Consulta el [README principal](../README.md) para arquitectura y pruebas.
