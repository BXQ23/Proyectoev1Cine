import { useEffect, useState } from 'react'
import { listarReservas, cancelarReserva } from '../api/reservas'
import { interpretarError } from '../api/errorHandler'
import StatusBanner from '../components/StatusBanner'

// Esta es la vista de "lectura protegible" que pide la pauta:
// hoy esta abierta, pero cuando llegue el JWT, solo un usuario
// logueado (scope reserva.read) va a poder cargar esto.
function MisReservas() {
  const [reservas, setReservas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState(null)

  const cargar = () => {
    setCargando(true)
    listarReservas()
      .then((res) => setReservas(res.data))
      .catch((err) => setError(interpretarError(err)))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])

  const handleCancelar = async (id) => {
    try {
      await cancelarReserva(id)
      setReservas((prev) => prev.filter((r) => r.id !== id))
    } catch (err) {
      setError(interpretarError(err))
    }
  }

  return (
    <section>
      <div className="page-header">
        <h1>Mis reservas</h1>
        <p>Estas son las entradas que has reservado.</p>
      </div>

      {cargando && <StatusBanner type="cargando">Cargando tus reservas…</StatusBanner>}
      {error && <StatusBanner type={error.tipo}>{error.mensaje}</StatusBanner>}
      {!cargando && !error && reservas.length === 0 && (
        <StatusBanner type="vacio">Aún no tienes reservas. Ve a la cartelera y elige una función.</StatusBanner>
      )}

      <ul className="reserva-list">
        {reservas.map((r) => (
          <li key={r.id} className="reserva-item">
            <div>
              <p className="reserva-item__pelicula">
                {r.funcion?.pelicula ?? `Función #${r.funcion?.id ?? '—'}`}
              </p>
              <p className="reserva-item__detalle">
                Asiento {r.asiento} · reservado el{' '}
                {new Date(r.fechaReserva).toLocaleDateString('es-CL')}
              </p>
            </div>
            <button className="btn btn--ghost" onClick={() => handleCancelar(r.id)}>
              Cancelar
            </button>
          </li>
        ))}
      </ul>
    </section>
  )
}

export default MisReservas
