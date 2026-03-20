package com.usto.api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String cookieAuthName = "JSESSIONID";

        String commitCount = readGitTotalCommitCount();
        String version = toSemverLike(commitCount);     // "2.3.4"

        System.out.println("VERSION=" + version);
        var url = SwaggerConfig.class.getClassLoader().getResource("git.properties");
        System.out.println("git.properties resource = " + url);

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

    private String readGitTotalCommitCount() {
        try (InputStream is = SwaggerConfig.class.getClassLoader().getResourceAsStream("git.properties")) {
            if (is == null) return "0";

            Properties props = new Properties();
            props.load(is);

            String value = props.getProperty("git.total.commit.count");
            return (value == null || value.isBlank()) ? "0" : value.trim();
        } catch (Exception e) {
            // 여기서 예외 나면 무조건 0으로
            return "0";
        }
    }

    private String toSemverLike(String commitCountString) {
        int n;
        try {
            n = Integer.parseInt(commitCountString.trim());
        } catch (Exception e) {
            return "0.0.0";
        }

        int major = n / 100;
        int minor = (n / 10) % 10;
        int patch = n % 10;

        return major + "." + minor + "." + patch;
    }
}

