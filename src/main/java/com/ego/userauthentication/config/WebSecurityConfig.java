package com.ego.userauthentication.config;

import com.ego.userauthentication.security.jwt.JwtAuthenticationEntryPoint;
import com.ego.userauthentication.security.jwt.JwtRequestFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtRequestFilter jwtRequestFilter;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WebSecurityConfig(
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtRequestFilter jwtRequestFilter,
            UserDetailsService userDetailsService,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    // AuthenticationManager Bean for use in controllers or services
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // SecurityFilterChain configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable) // Disable CSRF for JWT-based authentication
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/api/v1/index", "/success", "/login",
                                "/authenticate", "/register", "/api/v1/register",
                                "/api/v1/auth/authenticate" ).permitAll() // Public endpoints
                        .requestMatchers(
                                "/swagger-ui/**", "/v3/api-docs/**", "/asm-swagger.html",
                                "/api-docs/**", "/api-docs/swagger-config", "/asm-swagger").permitAll() // Swagger endpoints
                        .anyRequest().authenticated() // All other requests need authentication
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // Handle unauthorized requests
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden"); // Handle access denied requests
                        })
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session management
                );

        // Add the JWT request filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Define a CorsConfigurationSource bean to configure CORS settings
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); // Add allowed origins
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE")); // Allowed HTTP methods
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type")); // Allowed request headers
        configuration.setAllowCredentials(true); // Allow credentials (cookies, authorization headers)

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}