// Centralizamos aca los mensajes de estado que pide la pauta:
// exito (se maneja en la propia pagina), error, vacio, 401 y 403.
// Asi cada pagina solo decide QUE estado mostrar, no COMO se ve.
function StatusBanner({ type, children }) {
  return <div className={`status-banner status-banner--${type}`}>{children}</div>
}

export default StatusBanner
