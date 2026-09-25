package com.cine.benja.cine_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Punto de entrada de la aplicacion. Al correr este main(),
// Spring levanta el servidor embebido (Tomcat) y escanea todo
// el paquete com.cine.benja.cine_backend en busca de @RestController,
// @Entity, @Repository, etc.
@SpringBootApplication
public class CineBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(CineBackendApplication.class, args);
	}

}
