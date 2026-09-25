import { NavLink } from 'react-router-dom'
import LoginButton from './LoginButton'

// Navegacion minima entre las 3 vistas que pide la pauta:
// publica, de lectura y de accion/escritura.
function Navbar() {
  return (
    <header className="navbar">
      <div className="navbar__brand">
        <span className="navbar__dot" />
        Cine Nocturno
      </div>
      <nav className="navbar__links">
        <NavLink to="/" end className={({ isActive }) => (isActive ? 'is-active' : '')}>
          Cartelera
        </NavLink>
        <NavLink to="/reservas" className={({ isActive }) => (isActive ? 'is-active' : '')}>
          Mis reservas
        </NavLink>
      </nav>
      <div className="navbar__auth">
        <LoginButton />
      </div>
    </header>
  )
}

export default Navbar
