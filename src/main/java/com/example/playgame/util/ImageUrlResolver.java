package com.example.playgame.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Преобразует относительные пути изображений в полные URL.
 * Нужно для отдельного фронтенда (React): при деплое бэк и фронт на разных хостах
 * фронт получает полный URL (https://api.example.com/uploads/games/1/game1.png)
 * и может загрузить картинку с бэкенда.
 */
@Component
public class ImageUrlResolver {

    @Value("${app.api-base-url:http://localhost:8080}")
    private String apiBaseUrl;

    /**
     * @param relativePath путь из БД, например /uploads/games/1/game1.png
     * @return полный URL, например http://localhost:8080/uploads/games/1/game1.png
     */
    public String resolve(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return null;
        }
        if (relativePath.startsWith("http://") || relativePath.startsWith("https://")) {
            return relativePath;
        }
        String base = apiBaseUrl.endsWith("/") ? apiBaseUrl.substring(0, apiBaseUrl.length() - 1) : apiBaseUrl;
        return base + (relativePath.startsWith("/") ? relativePath : "/" + relativePath);
    }
}
