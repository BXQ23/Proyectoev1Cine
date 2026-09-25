package com.cine.benja.cine_backend.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

// Representa a un usuario YA verificado por Firebase (el token ya fue validado
// por FirebaseAuthenticationFilter antes de crear esto). El "principal" es
// nuestro propio AuthenticatedUser (no el FirebaseToken del SDK), para que
// los tests no dependan de Firebase para nada.
public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedUser usuario;

    public FirebaseAuthenticationToken(AuthenticatedUser usuario, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.usuario = usuario;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // no hay password/secreto que guardar aca, ya se valido el token
    }

    @Override
    public Object getPrincipal() {
        return usuario;
    }
}
