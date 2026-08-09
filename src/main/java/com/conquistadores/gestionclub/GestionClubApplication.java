package com.conquistadores.gestionclub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GestionClubApplication {
    public static void main(String[] args) {
        SpringApplication.run(GestionClubApplication.class, args);
    }
}
