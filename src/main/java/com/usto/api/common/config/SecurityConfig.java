package com.usto.api.common.config;

import com.usto.api.common.utils.SecurityContextPersistenceFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor // 생성자 주입용
public class SecurityConfig {

    private final Environment env; // 환경 변수 접근용

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 현재 활성화된 프로필이 dev인지 확인
        boolean isDev = Arrays.asList(env.getActiveProfiles()).contains("dev");

        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                //세션 자동 저장
                .securityContext(securityContext -> securityContext
                        .requireExplicitSave(false)
                        .securityContextRepository(securityContextRepository())
                )
                .addFilterBefore(
                        new SecurityContextPersistenceFilter(securityContextRepository()),
                        UsernamePasswordAuthenticationFilter.class
                )
                // 세션 기반 인증 구조
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) //필요할 때만(남용x)
                        .sessionFixation().changeSessionId() // 로그인 시 세션 ID 변경
                )
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((request, response, authException) -> {
                            String uri = request.getRequestURI();
                            String method = request.getMethod();
                            String cookie = request.getHeader("Cookie");
                            HttpSession session = request.getSession(false);

                            //상세 로그 추가
                            log.error("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                            log.error("┃  인증 실패: {} {}", method, uri);
                            log.error("┃  원인: {}", authException.getMessage());
                            log.error("┃  쿠키: {}", cookie != null ? "있음" : "없음");
                            if (cookie != null) {
                                log.error("┃    └─ {}", cookie);
                            }
                            log.error("┃  세션: {}", session != null ? "있음 (ID: " + session.getId() + ")" : "없음");
                            if (session != null) {
                                Object securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
                                log.error("┃    └─ SecurityContext in session: {}", securityContext != null ? "있음" : "없음");
                            }
                            log.error("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.error(" 권한 부족: {} {}", request.getMethod(), request.getRequestURI());
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"message\":\"접근권한이 없습니다.\"}");
                        })
                )

                // 역할별 경로 제한 로직
                .authorizeHttpRequests(auth -> {
                    // 기본 허용 경로 (언제든 접근 가능) - 아무 역할이 아니더라도
                    auth.requestMatchers(
                            "/api/users/sign-up", //회원가입
                            "/api/users/exists/**", //중복조회 when 회원가입
                            "/api/auth/find/**", //아이디/비밀번호 찾기
                            "/api/auth/verification/**", //이메일/전화번호 인증 when 회원가입,아이디/비번찾기
                            "/api/auth/login", //로그인
                            "/api/auth/logout", //로그아웃
                            "/api/approval/**", //일단은 열어두는데 추후에 막아야한다.
                            "/api/g2b/sync", //일단 열어두는데 추후에 막아야한다.
                            "/api/g2b/test",//일단은 열어두는데 추후에 막아야한다.
                            "/api/g2b/add-drbYr", //일단은 열어두는데 추후에 막아야한다.
                            "/api/g2b/test/usefulLife", //일단은 열어두는데 추후에 막아야한다.
                            "/api/public/**",
                            "/public/**", //타임리프용
                            "/images/**", //한양대 로고 보여주기 용
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/swagger-resources/**",
                            "/error"
                    ).permitAll();
                    //역할별 접근 제한
                    auth.requestMatchers("/api/item/returnings/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("/api/item/acquisitions/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("/api/item/disuses/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("/api/item/disposals/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("/api/item/assets/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("/api/**").hasAnyRole("MANAGER", "ADMIN");

                    auth.anyRequest().authenticated();
                })

                .formLogin(AbstractHttpConfigurer::disable) // no login we made it
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)// no login we made it
        ;


        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "https://u-sto.github.io",
                "http://localhost:3000",
                "http://localhost:8080",
                "http://localhost:5500", //로컬 테스트용
                "https://avengeful-shaunte-revolvingly.ngrok-free.dev" //로컬 테스트용
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // 세션 기반이면 true 유지

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    /**
     *
     * @param config
     * @return
     * @throws Exception
     */
    @Bean // 로그인에 필요함
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}