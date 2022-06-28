package com.seven.marketclip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MarketclipApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketclipApplication.class, args);
    }

}
