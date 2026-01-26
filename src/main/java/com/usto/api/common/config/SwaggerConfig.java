package com.usto.api.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("U-STO API Document")
                        .version("v1.0.0")
                        .description("U-STO 서비스의 API 명세서입니다."))
                // 핵심: 서버 URL을 '/' (상대 경로)로 설정
                // 이렇게 하면 http/https 상관없이 현재 접속한 도메인을 기준으로 요청을 보냅니다.
                .servers(List.of(
                        new Server().url("/").description("Default Server URL")
                ));
    }

    private io.swagger.v3.oas.models.info.Info apiInfo() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("U-sto API")
                .description("대학물품관리시스템")
                .version("1.0.0");


    }
}

