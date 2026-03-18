package com.indra.usuarios.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MsUsuariosReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsUsuariosReactiveApplication.class, args);
    }
}
