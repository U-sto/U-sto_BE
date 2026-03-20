package com.usto.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Optional;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(Optional<GitProperties> gitProperties) {
        String cookieAuthName = "JSESSIONID";

        String commitCount = gitProperties
                .map(g -> g.get("git.total.commit.count"))
                .map(Object::toString)
                .orElse("0");

        String version = "1.0." + commitCount;


        System.out.println("VERSION=" + version);

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(cookieAuthName);

        SecurityScheme securityScheme = new SecurityScheme()
                .name(cookieAuthName)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .scheme("cookie");

        return new OpenAPI()
                .info(new Info()
                        .title("U-sto BackEnd API")
                        .description("U-sto 백엔드 API 문서입니다.")
                        .version(version))
                .addSecurityItem(securityRequirement)
                .components(new Components()
                        .addSecuritySchemes(cookieAuthName, securityScheme));
    }
}

