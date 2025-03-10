package com.employee.benhvientu.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(); // Đăng ký UserDetailsServiceImpl
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Vô hiệu hóa CSRF
                .cors(cors -> cors.disable())  // Tạm thời tắt CORS để kiểm tra
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll() // Cho phép đăng ký không cần xác thực
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll() // Cho phép login không cần xác thực
                        .requestMatchers("/api/patient/profiles").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/patient/update").hasAnyAuthority("ROLE_ADM", "ROLE_EMP", "ROLE_MGR")
                        .requestMatchers(HttpMethod.POST, "/api/medical-records/create").hasAuthority("ROLE_MGR")
                        .requestMatchers(HttpMethod.POST, "/api/medical-records/list").hasAuthority("ROLE_MGR")
                        .requestMatchers(HttpMethod.GET, "/api/medical-records/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/patient/{id}").authenticated()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
