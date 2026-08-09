CREATE TABLE game (
    game_id BIGINT PRIMARY KEY,
    game_type VARCHAR(10) NOT NULL,
    season INT NOT NULL,
    game_date TIMESTAMP WITH TIME ZONE NOT NULL,
    game_state VARCHAR(50) NOT NULL,
    away_team_id BIGINT NOT NULL,
    away_team_name VARCHAR(255) NOT NULL,
    away_team_score INT NOT NULL,
    home_team_id BIGINT NOT NULL,
    home_team_name VARCHAR(255) NOT NULL,
    home_team_score INT NOT NULL
);