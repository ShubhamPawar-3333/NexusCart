package com.nexuscart.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for the API Gateway
 * Allows cross-origin requests from the Angular frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allow Angular dev server and production origins
        corsConfig.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://localhost:3000",
                "http://127.0.0.1:4200"));

        // Allow all common HTTP methods
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allow common headers including Authorization
        corsConfig.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        corsConfig.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        corsConfig.setMaxAge(3600L);

        // Expose custom headers to the client
        corsConfig.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Total-Count",
                "X-Page-Number",
                "X-Page-Size"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
