import api from './api'

// Estos tres, cuando este el backend protegido, van a requerir estar
// logueado (y el POST/DELETE ademas van a requerir el scope de escritura)
export const listarReservas = () => api.get('/api/reservas')

export const crearReserva = (reserva) => api.post('/api/reservas', reserva)

export const cancelarReserva = (id) => api.delete(`/api/reservas/${id}`)
