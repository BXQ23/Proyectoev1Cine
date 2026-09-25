import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Cartelera from './pages/Cartelera'
import MisReservas from './pages/MisReservas'
import Reservar from './pages/Reservar'

function App() {
  return (
    <div className="app">
      {/* Fondo decorativo: pointer-events: none, no bloquea clics en nada */}
      <div className="night-sky" aria-hidden="true">
        <div className="night-sky__moon" />
        <div className="night-sky__stars" />
      </div>
      <Navbar />
      <main className="app__content">
        <Routes>
          <Route path="/" element={<Cartelera />} />
          <Route path="/reservas" element={<MisReservas />} />
          <Route path="/reservar/:id" element={<Reservar />} />
        </Routes>
      </main>
    </div>
  )
}

export default App
