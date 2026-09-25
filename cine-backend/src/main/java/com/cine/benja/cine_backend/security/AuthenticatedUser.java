package com.cine.benja.cine_backend.security;

// Representa al usuario ya autenticado, con SOLO lo que el resto del backend
// necesita (uid, email, si es admin). Se arma una vez que
// FirebaseAuthenticationFilter ya verifico el token con el SDK de Firebase.
//
// Por que no usar directamente com.google.firebase.auth.FirebaseToken como
// principal: es una clase "final" del SDK, dificil de simular en los tests
// unitarios. Con esta clase propia, los tests solo hacen "new AuthenticatedUser(...)",
// sin tocar Firebase para nada.
public record AuthenticatedUser(String uid, String email, boolean admin) {
}
