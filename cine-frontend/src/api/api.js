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
  console.log('DEBUG usuarioActual:', usuarioActual)

  if (!usuarioActual) return null // nadie logueado -> se manda sin token (endpoints publicos)

  // getIdToken() renueva el token solo si ya esta por vencer - no hace falta
  // manejar "silent vs popup" como con MSAL, Firebase lo resuelve internamente.
  const token = await usuarioActual.getIdToken()
  console.log('DEBUG token obtenido:', token ? token.substring(0, 20) + '...' : null)
  return token
}

// Toda llamada al backend pasa por esta instancia.
// baseURL sale de VITE_API_BASE_URL (.env), nunca hardcodeada -
// asi cuando el backend pase detras del API Gateway, cambiamos
// una linea en .env y listo, sin tocar ninguna pagina.
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