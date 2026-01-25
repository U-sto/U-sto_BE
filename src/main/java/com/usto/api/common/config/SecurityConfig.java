package com.usto.api.common.config;

import com.usto.api.user.application.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
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
                // 세션 기반 인증 구조
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)) //필요할 때만(남용x)

                // 인증/인가에 대한 실패 응답을 API스럽게
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지 추가
                            response.getWriter().write("{\"message\":\"로그인이 필요합니다.\"}"); //로그인이 필요합니다(프론트)
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding("UTF-8"); // 한글 깨짐 방지 추가
                            response.getWriter().write("{\"message\":\"접근권한이 없습니다.\"}"); //접근 권한이 없습니다(프론트)
                        })
                )

                // Swagger
                .authorizeHttpRequests(auth -> {
                    // 기본 허용 경로 (언제든 접근 가능)
                    auth.requestMatchers(
                            "/api/users/sign-up", //회원가입
                            "/api/users/exists/**", //중복조회 when 회원가입
                            "/api/auth/find/**", //아이디/비밀번호 찾기
                            "/api/auth/verification/**", //이메일/전화번호 인증 when 회원가입,아이디/비번찾기
                            "/api/auth/login", //로그인
                            "/api/auth/logout", //로그아웃
                            "/api/approval/**", //일단은 열어두는데 추후에 막아야한다.
                            "/api/g2b/sync", //일단은 열어두는데 추후에 막아야한다.
                            "/api/g2b/test" //일단은 열어두는데 추후에 막아야한다.
                    ).permitAll();

                    //역할별 접근 제한
                    auth.requestMatchers("/api/item/acquisitions/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("/api/**").hasAnyRole("MANAGER", "ADMIN");

                    if (isDev) {
                        // 개발 환경(dev)일 때만 로그인 없이 스웨거 테스트하고 싶은 API 작성 가능

                    }

                    auth.requestMatchers(
                            "/v3/api-docs/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/swagger-resources/**"
                    ).permitAll();

                    auth.requestMatchers("/error").permitAll();

                    auth.anyRequest().authenticated();
                })

                .formLogin(AbstractHttpConfigurer::disable) // no login for swagger , we need comfort
                .httpBasic(AbstractHttpConfigurer::disable)
                //로그아웃
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"success\":true,\"message\":\"로그아웃 성공\",\"data\":null}");
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:8080")); // 프론트엔드 포트에 맞게 수정 (예: 3000)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        //세션 생성여부 확인
        configuration.setExposedHeaders(List.of("X-Session-Exists"));

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

    //아이디,비밀번호 로그인 실행기 (로그인 구현시에 사용할 예정)
    /**
     *\
     * @param userDetailsService
     * @param passwordEncoder
     * @return
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // 사용자 조회 전략
        provider.setPasswordEncoder(passwordEncoder);       // 비밀번호 비교 전략(핵심)
        return provider;
    }
}