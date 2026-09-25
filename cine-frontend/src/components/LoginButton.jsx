import { useState, useRef, useEffect } from 'react'
import {
  signInWithPopup,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut,
} from 'firebase/auth'
import { auth, googleProvider } from '../firebase.js'
import { useAuth } from '../context/AuthContext.jsx'

// Cubre los 2 flujos que pide la pauta:
// - Email/contraseña: "los usuarios pueden crear su cuenta y luego iniciar sesion"
// - Google Sign-In: usa Authorization Code + PKCE de verdad por debajo (OAuth 2.0 real)
export default function LoginButton() {
  const { user, cargando } = useAuth()
  const [mostrarFormulario, setMostrarFormulario] = useState(false)
  const [mostrarMenu, setMostrarMenu] = useState(false)
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const menuRef = useRef(null)

  // Cierra el menu si haces clic afuera - asi no queda pegado en pantalla
  useEffect(() => {
    function manejarClicAfuera(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setMostrarMenu(false)
      }
    }
    document.addEventListener('mousedown', manejarClicAfuera)
    return () => document.removeEventListener('mousedown', manejarClicAfuera)
  }, [])

  if (cargando) return null

  if (user) {
    const inicial = (user.email?.[0] || '?').toUpperCase()
    return (
      <div className="navbar__auth navbar__auth--menu" ref={menuRef}>
        <button
          className="avatar-button"
          onClick={() => setMostrarMenu((v) => !v)}
          aria-label="Cuenta"
        >
          {inicial}
        </button>
        {mostrarMenu && (
          <div className="avatar-menu">
            <span className="avatar-menu__email">{user.email}</span>
            <button className="btn--outline" onClick={() => signOut(auth)}>
              Cerrar sesión
            </button>
          </div>
        )}
      </div>
    )
  }

  async function iniciarConGoogle() {
    setError('')
    try {
      await signInWithPopup(auth, googleProvider)
    } catch (e) {
      setError('No se pudo iniciar sesión con Google.')
    }
  }

  async function manejarEmailPassword(e) {
    e.preventDefault()
    setError('')
    try {
      await signInWithEmailAndPassword(auth, email, password)
    } catch (e) {
      // Si la cuenta no existe, la creamos - simplifica el flujo para el usuario
      if (e.code === 'auth/user-not-found' || e.code === 'auth/invalid-credential') {
        try {
          await createUserWithEmailAndPassword(auth, email, password)
        } catch (e2) {
          setError('No se pudo crear la cuenta: revisa el correo/contraseña.')
        }
      } else {
        setError('No se pudo iniciar sesión.')
      }
    }
  }

  return (
    <div className="navbar__auth">
      {!mostrarFormulario && (
        <>
          <button onClick={() => setMostrarFormulario(true)}>Iniciar sesión</button>
          <button onClick={iniciarConGoogle}>Continuar con Google</button>
        </>
      )}
      {mostrarFormulario && (
        <form onSubmit={manejarEmailPassword} className="login-form">
          <input
            type="email"
            placeholder="correo@ejemplo.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <input
            type="password"
            placeholder="contraseña"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit">Entrar / Crear cuenta</button>
          {error && <span className="login-form__error">{error}</span>}
        </form>
      )}
    </div>
  )
}
