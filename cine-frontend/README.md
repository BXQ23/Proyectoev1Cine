# cine-frontend

Frontend React (Vite) del EP1 DSY1107 — dominio "Cine" (Funcion — Reserva).

## Cómo correrlo

1. Necesitas Node.js instalado (18+). Verifica con `node --version`.
2. Descomprime el zip y abre una terminal dentro de la carpeta `cine-frontend`.
3. Instala las dependencias:
   ```
   npm install
   ```
4. Levanta el servidor de desarrollo:
   ```
   npm run dev
   ```
5. Abre `http://localhost:3000` en el navegador.

**Importante:** el backend (`cine-backend`) debe estar corriendo en `http://localhost:8080` al mismo tiempo, si no vas a ver el error de "no se pudo contactar al servidor" en la cartelera.

## Estructura

```
src/
├── api/          # toda la comunicación con el backend vive aca
│   ├── api.js         # instancia de axios + interceptor (futuro JWT)
│   ├── funciones.js
│   ├── reservas.js
│   └── errorHandler.js
├── components/
│   ├── Navbar.jsx
│   └── StatusBanner.jsx   # muestra éxito / error / vacío / 401 / 403
├── pages/
│   ├── Cartelera.jsx      # pública
│   ├── MisReservas.jsx    # protegida a futuro (lectura)
│   └── Reservar.jsx       # protegida a futuro (escritura)
├── App.jsx        # rutas
└── main.jsx
```

## Configuración

La URL del backend está en `.env` (`VITE_API_BASE_URL`). Cuando el backend pase
detrás del API Gateway, solo cambia ese valor — ningún componente tiene la URL
escrita a mano.

## Pendiente (próximos pasos del EP1)

- Integrar `@azure/msal-react` para el login con Azure AD.
- Reemplazar el usuario "invitado@duoc.cl" hardcodeado en `Reservar.jsx` por el
  que venga del JWT.
- Descomentar la línea del interceptor en `api.js` para adjuntar el
  `Authorization: Bearer <token>` en cada llamada.
- Proteger las rutas `/reservas` y `/reservar/:id` con un guard que redirija al
  login si no hay sesión.
