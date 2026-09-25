package com.cine.benja.cine_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

// Inicializa el SDK de administracion de Firebase UNA sola vez al arrancar el backend.
// Este SDK es el que despues usa FirebaseAuthenticationFilter para verificar cada
// token que llega desde el frontend.
@Configuration
public class FirebaseConfig {

    // Ruta al .json de la cuenta de servicio, en TU maquina, fuera del repo.
    // Se configura en application.properties (firebase.credentials.path) o con la
    // variable de entorno FIREBASE_CREDENTIALS_PATH.
    @Value("${firebase.credentials.path}")
    private String credentialsPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }
        try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp firebaseApp) {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
