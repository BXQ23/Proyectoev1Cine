import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { obtenerFuncion } from '../api/funciones'
import { crearReserva } from '../api/reservas'
import { interpretarError } from '../api/errorHandler'
import StatusBanner from '../components/StatusBanner'

// Esta es la vista de "modificacion protegible" que pide la pauta:
// requiere estar logueado Y tener el permiso de escritura (reserva.write)
// una vez que conectemos Azure AD.
function Reservar() {
  const { id } = useParams()
  const navigate = useNavigate()

  const [funcion, setFuncion] = useState(null)
  const [asiento, setAsiento] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState(null)
  const [exito, setExito] = useState(false)

  useEffect(() => {
    obtenerFuncion(id)
      .then((res) => setFuncion(res.data))
      .catch((err) => setError(interpretarError(err)))
  }, [id])

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setEnviando(true)
    try {
      await crearReserva({
        funcion: { id: Number(id) },
        // TODO: reemplazar por el email/sub que venga del JWT una vez logueado
        usuario: 'invitado@duoc.cl',
        asiento,
      })
      setExito(true)
      setTimeout(() => navigate('/reservas'), 1200)
    } catch (err) {
      setError(interpretarError(err))
    } finally {
      setEnviando(false)
    }
  }

  return (
    <section className="reservar">
      <div className="page-header">
        <h1>Reservar asiento</h1>
        {funcion && (
          <p>
            {funcion.pelicula} · {funcion.sala} ·{' '}
            {new Date(funcion.horario).toLocaleString('es-CL', {
              day: 'numeric',
              month: 'short',
              hour: '2-digit',
              minute: '2-digit',
            })}
          </p>
        )}
      </div>

      {error && <StatusBanner type={error.tipo}>{error.mensaje}</StatusBanner>}
      {exito && <StatusBanner type="exito">Reserva confirmada. Redirigiendo…</StatusBanner>}

      <form className="form" onSubmit={handleSubmit}>
        <label className="form__field">
          <span>Número de asiento</span>
          <input
            type="text"
            placeholder="Ej: B12"
            value={asiento}
            onChange={(e) => setAsiento(e.target.value)}
            required
          />
        </label>

        <button className="btn btn--primary" type="submit" disabled={enviando}>
          {enviando ? 'Reservando…' : 'Confirmar reserva'}
        </button>
      </form>
    </section>
  )
}

export default Reservar
