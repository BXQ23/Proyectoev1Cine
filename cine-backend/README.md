# cine-backend (BFF)

BFF (Backend For Frontend) del proyecto EV1 DSY1107 — dominio "Cine Nocturno".

Este servicio **no tiene base de datos propia**. Su único trabajo es:
1. Validar el token que llega del frontend (Firebase Authentication + SDK de administración de Firebase).
2. Si el token es válido, reenviar la petición al microservicio de negocio real: **cine-core** (puerto 8081), decidiendo además qué puede ver/hacer cada usuario (dueño de la reserva vs. admin).

El CRUD de verdad (Funcion, Reserva, base de datos) vive en `cine-core` — ver su propio README.

## Cómo correrlo

**Levanta primero `cine-core` (puerto 8081) y después este proyecto.**

1. Corre `cine-core` (ver su README) — queda en `http://localhost:8081`.
2. Configura `firebase.credentials.path` en `application.properties` (ver sección de abajo).
3. Corre `CineBackendApplication.java`. Queda arriba en `http://localhost:8080`.

## Endpoints expuestos (hacia el frontend)

| Método | Ruta | Protegido |
|---|---|---|
| GET | `/api/public/funciones` | No — público |
| GET | `/api/public/funciones/{id}` | No — público |
| GET | `/api/reservas` | Sí — cualquier usuario logueado (ve solo las suyas, salvo admin) |
| GET | `/api/reservas/{id}` | Sí — solo el dueño o un admin |
| POST | `/api/reservas` | Sí — cualquier usuario logueado (queda a su nombre) |
| DELETE | `/api/reservas/{id}` | Sí — solo el dueño o un admin (si no, 403) |

Internamente cada uno reenvía a `cine-core` (`/internal/...`) por HTTP con `RestTemplate`
(`RestTemplateConfig.java`). La URL de `cine-core` es configurable vía `core.service.url`
(`application.properties` o variable de entorno `CORE_SERVICE_URL`).

## Seguridad (Firebase Authentication)

Piezas nuevas:
- `FirebaseConfig.java`: inicializa el SDK de administración de Firebase al arrancar, leyendo el `.json` de la cuenta de servicio.
- `FirebaseAuthenticationFilter.java`: en cada request, si viene `Authorization: Bearer <token>`, lo verifica contra Firebase (firma, expiración, proyecto correcto) y arma la autenticación.
- `AuthenticatedUser.java` / `FirebaseAuthenticationToken.java`: representan al usuario ya verificado (uid, email, si es admin), desacoplados del SDK para que los tests no necesiten hablar con Firebase.
- `SecurityConfig.java`: `/api/public/**` libre, todo lo demás exige estar autenticado. Sin token → 401. La diferencia dueño/admin (403 si intentas tocar la reserva de otro) se resuelve dentro de `ReservaController.java`, no en esta config.

### Cómo conseguir el `.json` de la cuenta de servicio

1. Firebase Console → tu proyecto → ⚙️ Configuración del proyecto → pestaña "Cuentas de servicio".
2. "Generar nueva clave privada" → descarga el `.json`.
3. Guárdalo en tu máquina, **fuera de la carpeta del repo** (o en la carpeta del repo, pero está en `.gitignore` — revisa que el nombre coincida con el patrón `*firebase-adminsdk*.json`).
4. Pon esa ruta completa en `application.properties`, en `firebase.credentials.path`.

### Cómo dar el rol de admin a un usuario (segundo nivel de acceso)

Los custom claims no se ponen desde la consola de Firebase, se asignan con el SDK. Ejemplo de script rápido (Node.js, fuera de este proyecto):

```js
const admin = require('firebase-admin');
admin.initializeApp({ credential: admin.credential.cert(require('./tu-cuenta-servicio.json')) });
admin.auth().setCustomUserClaims('UID_DEL_USUARIO', { admin: true })
  .then(() => console.log('listo'));
```

El usuario tiene que volver a loguearse (o forzar refresh del token) para que el nuevo claim aparezca en su próximo ID Token.

## Tests

`SecurityConfigTest.java` prueba la matriz de seguridad (público→2xx, sin token→401,
reserva ajena→403, reserva propia o admin→2xx) sin llamar a Firebase de verdad —
se inyecta directamente un `FirebaseAuthenticationToken` de prueba.

```
mvn test
```

## Pendiente

- Confirmar login end-to-end desde el frontend (email/contraseña y Google Sign-In).
- Asignar el custom claim `admin` a al menos un usuario de prueba, para demostrar el 403.
- Desplegar `cine-backend` + `cine-core` en EC2 (con RAM suficiente) y exponer `cine-backend`
  a través de AWS API Gateway — `cine-core` no debe quedar expuesto a internet.
