package com.cine.benja.cine_backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/** Firebase Admin se activa solo con APP_AUTH_MODE=firebase. La clave JSON nunca va dentro del ZIP. */
@Configuration
@ConditionalOnProperty(name = "app.auth.mode", havingValue = "firebase")
public class FirebaseConfig {

    @Bean
    public FirebaseAuth firebaseAuth(@Value("${firebase.credentials.path:}") String credentialsPath) throws IOException {
        FirebaseApp app;
        if (!FirebaseApp.getApps().isEmpty()) {
            app = FirebaseApp.getInstance();
        } else {
            if (credentialsPath == null || credentialsPath.isBlank()) {
                throw new IllegalStateException(
                    "Falta FIREBASE_CREDENTIALS_PATH. Define la ruta al JSON de cuenta de servicio o usa APP_AUTH_MODE=dev solo localmente."
                );
            }
            try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
                app = FirebaseApp.initializeApp(options);
            }
        }
        return FirebaseAuth.getInstance(app);
    }
}
