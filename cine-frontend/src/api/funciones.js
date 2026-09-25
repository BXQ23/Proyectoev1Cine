import api from './api'

// Endpoint publico: no necesita token
export const listarFunciones = () => api.get('/api/public/funciones')

export const obtenerFuncion = (id) => api.get(`/api/public/funciones/${id}`)
