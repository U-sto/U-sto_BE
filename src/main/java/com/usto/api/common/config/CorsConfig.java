package com.usto.api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "https://u-sto.github.io",
                                "http://localhost:3000", // 필요 시 로컬 프론트 테스트용
                                "http://localhost:8080", // 필요 시 백엔드 프론트 테스트용
                                "http://localhost:5173", // 필요 시 로컬 프론트 테스트용
                                "http://localhost:5174", // 필요 시 로컬 프론트 테스트용
                                "http://13.124.10.41:3000",
                                "http://13.124.10.41:8080",
                                "http://13.124.10.41:5173",
                                "https://u-sto-fe.vercel.app" //프론트 배포
                        )
                        .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
