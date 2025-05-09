CREATE OR REPLACE FUNCTION update_game_rating() RETURNS TRIGGER AS $$
BEGIN
   IF TG_OP = 'INSERT' THEN
UPDATE games
SET rating = (SELECT AVG(rating) FROM games_ratings WHERE game_id = NEW.game_id)
WHERE id = NEW.game_id;
ELSIF TG_OP = 'DELETE' THEN
UPDATE games
SET rating = (SELECT AVG(rating) FROM games_ratings WHERE game_id = OLD.game_id)
WHERE id = OLD.game_id;
END IF;

RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER game_rating_update
    AFTER INSERT OR UPDATE OR DELETE ON games_ratings
    FOR EACH ROW
    EXECUTE FUNCTION update_game_rating();


