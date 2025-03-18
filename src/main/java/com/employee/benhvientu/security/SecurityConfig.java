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
                        .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll() // Cho phép quên mật khẩu không cần xác thực
                        .requestMatchers("/api/patient/profiles").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/patient/profile/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/patient/profile/update").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/patient/Doctorprofile").hasAuthority("ROLE_MGR") // Thêm dòng này
                        .requestMatchers(HttpMethod.GET, "/api/medicines").permitAll() // Ai cũng xem được danh sách thuốc
                        .requestMatchers(HttpMethod.POST, "/api/cart/add").hasAnyAuthority("ROLE_EMP", "ROLE_MGR") // Thêm vào giỏ
                        .requestMatchers(HttpMethod.GET, "/api/cart").hasAnyAuthority("ROLE_EMP", "ROLE_MGR") // Xem giỏ hàng
                        .requestMatchers(HttpMethod.PUT, "/api/cart/**").hasAnyAuthority("ROLE_EMP", "ROLE_MGR") // Cập nhật số lượng
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/**").hasAnyAuthority("ROLE_EMP", "ROLE_MGR") // Xóa khỏi giỏ
                        .requestMatchers(HttpMethod.POST, "/api/admin/create-doctor").hasAuthority("ROLE_ADM")
                        .requestMatchers(HttpMethod.POST, "/api/medical-records/create/{id}").hasAuthority("ROLE_MGR")
                        .requestMatchers(HttpMethod.POST, "/api/medical-records/list").hasAuthority("ROLE_MGR")
                        .requestMatchers(HttpMethod.GET, "/api/medical-records/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST,"/api/chat/session").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
