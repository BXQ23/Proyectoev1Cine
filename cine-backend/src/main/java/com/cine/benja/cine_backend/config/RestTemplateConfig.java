package com.cine.benja.cine_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Cliente HTTP simple que el BFF usa para hablarle a cine-core.
    // Todo lo que llega aca ya paso por SecurityConfig (JWT validado),
    // asi que cine-core no necesita volver a verificar nada.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
