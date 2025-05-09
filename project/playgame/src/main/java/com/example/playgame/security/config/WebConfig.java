package com.example.playgame.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class WebConfig {

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Разрешаем запросы с фронтенда
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        // Разрешаем указанные HTTP методы
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        // Разрешаем все заголовки
        config.setAllowedHeaders(List.of("*"));
        // Разрешаем отправку cookies и авторизационных данных
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Применяем конфигурацию ко всем путям
        source.registerCorsConfiguration("/**", config);

        return source;
    }

}
