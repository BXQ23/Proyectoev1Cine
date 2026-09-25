import { initializeApp } from 'firebase/app'
import { getAuth, GoogleAuthProvider } from 'firebase/auth'

// Pega aca el "firebaseConfig" que te dio la consola de Firebase al crear
// la app web (Configuracion del proyecto > Tus apps > </> Web).
// Estos valores NO son secretos (van a la vista de cualquiera igual, es
// normal que esten en el frontend) - el secreto de verdad es el .json de
// la cuenta de servicio, y ESE se queda solo en el backend.
const firebaseConfig = {
  apiKey: 'AIzaSyAE0qmVpeTbZA1S3iGRSBc-joCuK1bsGx0',
  authDomain: 'cine-nocturno-3e464.firebaseapp.com',
  projectId: 'cine-nocturno-3e464',
  storageBucket: 'cine-nocturno-3e464.firebasestorage.app',
  messagingSenderId: '986766301912',
  appId: '1:986766301912:web:b6fccfcefce00a5a9b55ad',
}

const firebaseApp = initializeApp(firebaseConfig)

export const auth = getAuth(firebaseApp)
export const googleProvider = new GoogleAuthProvider()
