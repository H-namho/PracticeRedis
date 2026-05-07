package com.example.swaggerprac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SwaggerPracApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwaggerPracApplication.class, args);
    }

}
