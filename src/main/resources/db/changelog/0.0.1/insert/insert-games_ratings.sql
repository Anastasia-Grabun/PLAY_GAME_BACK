INSERT INTO games_ratings (account_id, game_id, rating)
SELECT p.owner_id, p.game_id,
       CASE
           WHEN p.owner_id = 2 AND p.game_id = 3 THEN 4.5
           WHEN p.owner_id = 3 AND p.game_id = 1 THEN 4.0
           WHEN p.owner_id = 5 AND p.game_id = 6 THEN 4.2
           WHEN p.owner_id = 6 AND p.game_id = 4 THEN 4.8
           WHEN p.owner_id = 8 AND p.game_id = 7 THEN 4.3
           WHEN p.owner_id = 9 AND p.game_id = 10 THEN 4.6
           WHEN p.owner_id = 11 AND p.game_id = 2 THEN 4.1
           WHEN p.owner_id = 12 AND p.game_id = 5 THEN 4.7
           WHEN p.owner_id = 14 AND p.game_id = 11 THEN 4.4
           WHEN p.owner_id = 16 AND p.game_id = 8 THEN 4.0
           WHEN p.owner_id = 17 AND p.game_id = 9 THEN 4.3
           WHEN p.owner_id = 19 AND p.game_id = 12 THEN 4.5
           WHEN p.owner_id = 21 AND p.game_id = 14 THEN 4.6
           WHEN p.owner_id = 22 AND p.game_id = 15 THEN 4.2
           WHEN p.owner_id = 24 AND p.game_id = 16 THEN 4.0
           WHEN p.owner_id = 25 AND p.game_id = 17 THEN 4.7
           WHEN p.owner_id = 27 AND p.game_id = 18 THEN 4.8
           WHEN p.owner_id = 28 AND p.game_id = 19 THEN 4.4
           WHEN p.owner_id = 30 AND p.game_id = 20 THEN 4.1
           WHEN p.owner_id = 31 AND p.game_id = 21 THEN 4.9
           WHEN p.owner_id = 32 AND p.game_id = 22 THEN 4.3
           WHEN p.owner_id = 33 AND p.game_id = 23 THEN 4.2
           WHEN p.owner_id = 34 AND p.game_id = 24 THEN 4.6
           WHEN p.owner_id = 35 AND p.game_id = 25 THEN 4.5
           WHEN p.owner_id = 36 AND p.game_id = 26 THEN 4.3
           WHEN p.owner_id = 37 AND p.game_id = 27 THEN 4.0
           WHEN p.owner_id = 39 AND p.game_id = 28 THEN 4.4
           WHEN p.owner_id = 40 AND p.game_id = 29 THEN 4.6
           ELSE 0.0
           END AS rating
FROM purchases p
         JOIN games g ON p.game_id = g.id;
