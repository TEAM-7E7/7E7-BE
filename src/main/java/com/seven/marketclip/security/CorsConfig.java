package com.seven.marketclip.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class CorsConfig implements WebMvcConfigurer {
//
//    @Bean
//    public CorsFilter corsFilter() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true); //내 서버가 응답을 할 때 json을 자바스크립트에서 처리할 수 있게 할지를 설정하는 것
//        config.addAllowedOrigin("*"); //모든 아이피에 응답을 허용하겠다.
//        config.addAllowedHeader("*"); //모든 헤더에 응답 허용
//        config.addAllowedMethod("*"); //모든 요청메서드에 허용
//        config.addExposedHeader("Authorization");
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }
//}
