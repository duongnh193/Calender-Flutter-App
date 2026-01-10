package com.duong.lichvanien.common.config;

import com.duong.lichvanien.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration with JWT authentication.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for REST API
                .csrf(AbstractHttpConfigurer::disable)
                
                // Enable CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Stateless session (JWT-based)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - Swagger
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        
                        // Public endpoints - Actuator
                        .requestMatchers(
                                "/actuator/**"
                        ).permitAll()
                        
                        // Public endpoints - User registration and login
                        .requestMatchers(
                                "/api/v1/user/register",
                                "/api/v1/user/login",
                                "/api/v1/user/refresh",
                                "/api/v1/user/fingerprint",
                                "/api/v1/user/fingerprint/current"
                        ).permitAll()
                        
                        // Public endpoints - Calendar, Zodiac (read-only)
                        .requestMatchers(HttpMethod.GET, "/api/v1/calendar/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/zodiac/**").permitAll()
                        
                        // Public endpoints - Horoscope (basic info)
                        .requestMatchers(HttpMethod.GET, "/api/v1/horoscope/daily/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/horoscope/monthly/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/horoscope/yearly/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/horoscope/can-chi/**").permitAll()
                        
                        // Public endpoints - Tu Vi chart generation (not interpretation)
                        .requestMatchers(HttpMethod.POST, "/api/v1/tuvi/chart").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tuvi/chart").permitAll()
                        
                        // Payment endpoints - public for creating and checking
                        .requestMatchers(
                                "/api/v1/payment/create",
                                "/api/v1/payment/check/**",
                                "/api/v1/payment/callback",
                                "/api/v1/payment/transaction/**"
                        ).permitAll()
                        
                        // Protected endpoints - User profile management
                        .requestMatchers(
                                "/api/v1/user/me",
                                "/api/v1/user/change-password",
                                "/api/v1/user/logout",
                                "/api/v1/user/deactivate"
                        ).authenticated()
                        
                        // Protected endpoints - Payment history
                        .requestMatchers(
                                "/api/v1/payment/history",
                                "/api/v1/payment/access/user"
                        ).authenticated()
                        
                        // Protected endpoints - Xu management
                        .requestMatchers(
                                "/api/v1/xu/**"
                        ).authenticated()
                        
                        // Protected endpoints - Affiliate
                        .requestMatchers(
                                "/api/v1/affiliate/**"
                        ).authenticated()
                        
                        // Public endpoints - SePay webhook
                        .requestMatchers(
                                "/api/v1/sepay/webhook"
                        ).permitAll()
                        
                        // Protected endpoints - Tu Vi interpretation (requires payment/xu check)
                        .requestMatchers("/api/v1/tuvi/chart/interpretation/**").permitAll()
                        
                        // Public endpoints - Tu Vi Grok interpretation
                        // /cycles is FREE, /full requires xu (checked in controller)
                        .requestMatchers("/api/v1/tuvi/grok/interpretation/cycles/**").permitAll()
                        .requestMatchers("/api/v1/tuvi/grok/interpretation/full/**").permitAll()
                        .requestMatchers("/api/v1/tuvi/grok/status").permitAll()
                        
                        // Protected endpoints - Lifetime horoscope (requires payment check via interceptor)
                        .requestMatchers("/api/v1/horoscope/lifetime/**").permitAll()
                        
                        // Admin endpoints
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/payment/admin/**").hasRole("ADMIN")
                        
                        // All other requests require authentication
                        .anyRequest().permitAll()
                )
                
                // Disable form login and HTTP basic
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                
                // Add JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // Configure for production
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", 
                "Content-Type", 
                "X-Fingerprint-Data",
                "X-Platform",
                "X-Chart-Hash"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
