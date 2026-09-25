import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarFunciones } from '../api/funciones'
import { interpretarError } from '../api/errorHandler'
import StatusBanner from '../components/StatusBanner'

// Vista publica: no requiere login. Cualquiera que entre a la app
// ve esto primero.
function Cartelera() {
  const [funciones, setFunciones] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    listarFunciones()
      .then((res) => setFunciones(res.data))
      .catch((err) => setError(interpretarError(err)))
      .finally(() => setCargando(false))
  }, [])

  return (
    <section>
      <div className="hero">
        <p className="hero__eyebrow">Esta semana en pantalla</p>
        <h1 className="hero__title">La cartelera enciende sus luces</h1>
        <p className="hero__subtitle">
          Elige una función y reserva tu asiento en segundos. Sin filas, sin apuros.
        </p>
      </div>

      {cargando && <StatusBanner type="cargando">Cargando la cartelera…</StatusBanner>}
      {error && <StatusBanner type={error.tipo}>{error.mensaje}</StatusBanner>}
      {!cargando && !error && funciones.length === 0 && (
        <StatusBanner type="vacio">Todavía no hay funciones programadas.</StatusBanner>
      )}

      <div className="ticket-grid">
        {funciones.map((f) => (
          <article key={f.id} className="ticket">
            <div className="ticket__main">
              <p className="ticket__sala">{f.sala}</p>
              <h2 className="ticket__pelicula">{f.pelicula}</h2>
              <p className="ticket__horario">
                {new Date(f.horario).toLocaleString('es-CL', {
                  weekday: 'short',
                  day: 'numeric',
                  month: 'short',
                  hour: '2-digit',
                  minute: '2-digit',
                })}
              </p>
            </div>
            <div className="ticket__stub">
              <p className="ticket__asientos">{f.asientosDisponibles}</p>
              <p className="ticket__asientos-label">asientos libres</p>
              <Link to={`/reservar/${f.id}`} className="ticket__cta">
                Reservar
              </Link>
            </div>
          </article>
        ))}
      </div>
    </section>
  )
}

export default Cartelera
