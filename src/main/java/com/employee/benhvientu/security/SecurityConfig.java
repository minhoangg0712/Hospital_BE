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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Các endpoint công khai
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/medicines").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/chat/session").permitAll()

                        // Các endpoint yêu cầu xác thực
                        .requestMatchers("/api/patient/profiles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/patient/profile/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/patient/profile").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/patient/profile/update").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/patient/Doctorprofile").hasAnyAuthority("ROLE_DOCTOR", "ROLE_MGR")
                        .requestMatchers(HttpMethod.GET, "/api/medical-records/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/medical-records/create-relative/{patientId}/{appointmentId}").hasAuthority("ROLE_MGR")
                        .requestMatchers(HttpMethod.GET, "/api/medical-records/patient/{id}").authenticated()

                        // Các endpoint cho giỏ hàng
                        .requestMatchers(HttpMethod.POST, "/api/cart/add").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/cart").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/cart/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/**").authenticated()

                        // Các endpoint cho admin và manager
                        .requestMatchers(HttpMethod.POST, "/api/admin/create-doctor").hasAuthority("ROLE_ADM")
                        .requestMatchers(HttpMethod.POST, "/api/medical-records/create/{id}").hasAuthority("ROLE_MGR")
                        .requestMatchers(HttpMethod.POST, "/api/medical-records/list").hasAuthority("ROLE_MGR")

                        // Phân quyền cho AppointmentController
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasAuthority("ROLE_EMP")
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/**").hasAuthority("ROLE_EMP")
                        .requestMatchers(HttpMethod.DELETE, "/api/appointments/**").hasAuthority("ROLE_EMP")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/**").hasAuthority("ROLE_EMP")
                        .requestMatchers(HttpMethod.GET, "api/departments/**").permitAll()

                        // Bác sĩ chỉ có thể xem danh sách lịch hẹn
                        .requestMatchers(HttpMethod.GET, "/api/appointments").hasAuthority("ROLE_MGR")
                        .requestMatchers(HttpMethod.GET, "/api/appointments/department").hasAuthority("ROLE_MGR")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}