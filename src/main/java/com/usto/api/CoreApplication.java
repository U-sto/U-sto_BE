package com.usto.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class CoreApplication {

    private final Environment env;

    public CoreApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @jakarta.annotation.PostConstruct
    void logProfile() {
        System.out.println("CoreApplication 실행 성공");
        System.out.println("profile 값(의도와 다르면 변경) : " + String.join(",", env.getActiveProfiles()));
        System.out.println("USERNAME(의도와 다르면 변경) : " + env.getProperty("spring.test.username"));
    }
}

