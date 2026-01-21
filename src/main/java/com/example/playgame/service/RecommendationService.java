package com.example.playgame.service;

import com.example.playgame.dto.game.GameShortcutResponseDto;

import java.util.List;

public interface RecommendationService {
    List<GameShortcutResponseDto> getRecommendations(String authHeader, int limit, int page);
}
