package com.example.moneyway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class MoneyWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoneyWayApplication.class, args);
    }
}
