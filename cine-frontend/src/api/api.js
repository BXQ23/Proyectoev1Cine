import axios from 'axios'
import { auth } from '../firebase.js'

async function obtenerAccessToken() {
  // Firebase restaura la sesion guardada de forma ASINCRONA al cargar la
  // pagina. Sin esto, si una peticion sale apenas la app arranca (ej. al
  // recargar estando en "Mis reservas"), auth.currentUser todavia puede
  // ser null aunque el usuario SI este logueado - authStateReady() espera
  // a que Firebase termine de resolver eso antes de seguir.
  await auth.authStateReady()

  const usuarioActual = auth.currentUser

  if (!usuarioActual) return null // nadie logueado -> se manda sin token (endpoints publicos)

  // getIdToken() renueva el token solo si ya esta por vencer - no hace falta
  // manejar "silent vs popup" como con MSAL, Firebase lo resuelve internamente.
  const token = await usuarioActual.getIdToken()
  return token
}

// Toda llamada al backend pasa por esta instancia.
// La URL sale de VITE_API_BASE_URL (.env), sin duplicarla en las paginas.
// En AWS, Nginx publica el frontend y reenvia /api/ al backend.
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Interceptor de request: inyecta el header "Authorization: Bearer <token>"
// en cada llamada automaticamente (si hay alguien logueado), sin tener que
// acordarnos de hacerlo a mano en cada pantalla.
api.interceptors.request.use(async (config) => {
  const token = await obtenerAccessToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

export default api