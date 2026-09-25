// Convierte cualquier error de axios en algo simple que la UI pueda
// usar directamente para elegir el mensaje/estado a mostrar.
// Esto es lo que despues nos deja "ver" facilmente los 401 y 403
// una vez que el backend quede protegido con el JWT.
export function interpretarError(error) {
  if (!error.response) {
    return { tipo: 'red', mensaje: 'No se pudo contactar al servidor. ¿Está corriendo el backend?' }
  }

  const { status } = error.response

  switch (status) {
    case 401:
      return { tipo: 'no-autenticado', mensaje: 'Tienes que iniciar sesión para ver esto.' }
    case 403:
      return { tipo: 'sin-permiso', mensaje: 'Tu cuenta no tiene permiso para esta acción.' }
    case 404:
      return { tipo: 'no-encontrado', mensaje: 'No encontramos lo que buscabas.' }
    case 400:
      return { tipo: 'validacion', mensaje: 'Revisa los datos ingresados: algo no es válido.' }
    default:
      return { tipo: 'error', mensaje: 'Ocurrió un error inesperado. Intenta de nuevo.' }
  }
}
