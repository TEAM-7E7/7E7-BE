package com.seven.marketclip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@ServletComponentScan
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@EnableCaching
public class MarketclipApplication {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customizer(){
        return p -> {
            p.setOneIndexedParameters(true);  // 페이지가 0이 아닌 1부터 시작하게 설정
            p.setMaxPageSize(10);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(MarketclipApplication.class, args);
    }

    @PostConstruct
    public void started() {
        // timezone UTC 셋팅
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

}
