import { createContext, useContext, useEffect, useState } from 'react'
import { onAuthStateChanged } from 'firebase/auth'
import { auth } from '../firebase.js'

// Firebase no trae un "Provider" listo como MsalProvider - onAuthStateChanged
// es la forma oficial de escuchar cuando el usuario inicia/cierra sesion,
// asi que este contexto es el reemplazo minimo de eso.
const AuthContext = createContext({ user: null, cargando: true })

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [cargando, setCargando] = useState(true)

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (usuarioActual) => {
      setUser(usuarioActual)
      setCargando(false)
    })
    return unsubscribe
  }, [])

  return (
    <AuthContext.Provider value={{ user, cargando }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
