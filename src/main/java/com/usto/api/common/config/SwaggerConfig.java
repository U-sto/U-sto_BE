package com.usto.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String cookieAuthName = "JSESSIONID";

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(cookieAuthName);

        SecurityScheme securityScheme = new SecurityScheme()
                .name(cookieAuthName)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .scheme("cookie");

        return new OpenAPI()
                .info(new Info()
                        .title("U-sto API")
                        .description("U-sto 백엔드 API 문서")
                        .version("1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes(cookieAuthName, securityScheme));
    }

    private io.swagger.v3.oas.models.info.Info apiInfo() {
        return new io.swagger.v3.oas.models.info.Info()
                .title("U-sto API")
                .description("대학물품관리시스템")
                .version("1.0.0");


    }
}

