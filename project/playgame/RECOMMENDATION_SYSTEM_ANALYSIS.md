# Анализ рекомендательной системы игр

## 1. Места, где делаются запросы для рекомендаций (SQL/ORM)

### GameRepository (`GameRepository.java`)

| Метод | Тип | Назначение |
|-------|-----|------------|
| `findRecommendedGamesByGenres` | Native SQL | Игры по любимым жанрам, исключая купленные, сортировка: число совпадений жанров ↓, рейтинг ↓ |
| `findRecommendedGamesByGenresPaged` | Native SQL + countQuery | То же с пагинацией (используется в рекомендациях) |
| `findTopRatedGamesExcluding` | JPQL | Fallback: топ по рейтингу, исключая переданный список ID |

Фрагмент основной выборки (жанры + исключения):

```62:77:project/playgame/src/main/java/com/example/playgame/repository/GameRepository.java
    @Query(value = """
            SELECT g.*
            FROM (
                SELECT gg.game_id AS game_id,
                       COUNT(DISTINCT gg.genre_id) AS genre_match_count
                FROM games_genres gg
                WHERE gg.genre_id IN :favoriteGenreIds
                GROUP BY gg.game_id
            ) m
            JOIN games g ON g.id = m.game_id
            WHERE g.id NOT IN :excludedGameIds
            ORDER BY m.genre_match_count DESC, g.rating DESC
            """, nativeQuery = true)
```

### GenreRepository (`GenreRepository.java`)

| Метод | Тип | Назначение |
|-------|-----|------------|
| `findFavouriteGenresByAccountId` | Native SQL | Список ID любимых жанров пользователя |
| `countFavouriteGenresByAccountId` | Native SQL | Количество любимых жанров (проверка порога) |

### PurchaseRepository (`PurchaseRepository.java`)

| Метод | Тип | Назначение |
|-------|-----|------------|
| `findGameIdsByOwnerId` | JPQL | ID игр, уже купленных пользователем (исключаются из рекомендаций) |

---

## 2. Текущий алгоритм

Тип: **контентный (content-based) с правилами**.

- **Признаки**: используются атрибуты сущности (жанры игр и профиль «любимые жанры» пользователя).
- **Правила**:
  1. Игра должна содержать хотя бы один из любимых жанров пользователя.
  2. Игра не должна быть в списке купленных (`excludedGameIds`).
  3. Сортировка: больше совпадающих жанров → выше, при равенстве — выше рейтинг.
  4. После выборки применяется фильтр разнообразия по жанрам (`ensureGenreDiversity`).

Коллаборативная фильтрация («похожие пользователи» / «игры, которые брали похожие пользователи») **не используется**.

---

## 3. Проблема: когда ни одна игра не подходит

### Уже обработано в коде

- **Ноль игр по жанрам** (все подходящие куплены или нет игр в этих жанрах): после `findRecommendedGamesByGenresPaged` вызывается fallback `findTopRatedGamesExcluding` — возвращаются топовые по рейтингу игры, кроме купленных.

```84:92:project/playgame/src/main/java/com/example/playgame/service/impl/RecommendationServiceImpl.java
        if (games.isEmpty()) {
            // Fallback: if nothing matched by genres (or native paging/count quirks), return top-rated not owned
            Page<Game> fallback = gameRepository.findTopRatedGamesExcluding(
                    ownedGameIds,
                    PageRequest.of(page, limit)
            );
            return fallback.stream()
                    .map(gameDtoMapper::gameToGameShortcutResponseDto)
                    .collect(Collectors.toList());
        }
```

### Проблемное место

- **Пустой список любимых жанров при вызове `getRecommendedGames`**: если `findFavouriteGenresByAccountId(accountId)` вернул пустой список, метод сразу возвращает пустой список и **fallback не вызывается**.

```60:63:project/playgame/src/main/java/com/example/playgame/service/impl/RecommendationServiceImpl.java
        List<Long> favouriteGenreIds = genreRepository.findFavouriteGenresByAccountId(accountId);
        if (favouriteGenreIds.isEmpty()) {
            return Collections.emptyList();
        }
```

В результате при расхождении между `countFavouriteGenresByAccountId` (≥ 3) и пустым `findFavouriteGenresByAccountId` (или при последующем изменении данных) пользователь получает пустой ответ, хотя мог бы получить хотя бы топ по рейтингу.

Дополнительно: для fallback в `findTopRatedGamesExcluding` в JPQL передаётся список исключений; при пустом списке в части диалектов «NOT IN ()» может вести себя неочевидно. В коде при отсутствии купленных игр подставляется `Collections.singletonList(-1L)`, что избегает пустого `IN` — это сделано корректно.

---

## 4. Предложенные улучшения (с кодом)

### 4.1. Использовать fallback при пустых любимых жанрах

Вместо возврата пустого списка при `favouriteGenreIds.isEmpty()` вызывать тот же fallback, что и при пустой выборке по жанрам (топ по рейтингу без купленных). Так мы устраняем описанную проблему.

**Файл:** `RecommendationServiceImpl.java`  
**Идея:** убрать ранний `return Collections.emptyList()` при пустых жанрах и строить `ownedGameIds`; затем вызывать `findTopRatedGamesExcluding` с пагинацией и маппингом в DTO (аналогично существующему fallback-блоку).

### 4.2. Защита от пустого списка в JPQL fallback

Оставить единую точку формирования списка исключений (например, «если пусто — подставить `[-1L]`») и везде передавать в `findTopRatedGamesExcluding` только непустой список, чтобы поведение было предсказуемым во всех диалектах.

### 4.3. Тесты

- Добавить тест: у пользователя 3+ любимых жанров по счётчику, но `findFavouriteGenresByAccountId` возвращает пустой список — ожидаем ответ от fallback (топ по рейтингу), а не пустой список.
- Исправить существующие тесты, которые мокают `findTopRatedGames` вместо `findRecommendedGamesByGenresPaged` / `findTopRatedGamesExcluding`, чтобы они соответствовали реальному сценарию рекомендаций и fallback.

Ниже — конкретные правки в коде по пунктам 4.1 и 4.2.
