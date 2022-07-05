package com.seven.marketclip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@ServletComponentScan
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class MarketclipApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketclipApplication.class, args);
    }

}
