DROP TABLE IF EXISTS troops;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS buildings;
DROP TABLE IF EXISTS locations;
DROP TABLE IF EXISTS kingdoms;
DROP TABLE IF EXISTS players;

CREATE TABLE players (
	id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    points INTEGER NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE kingdoms (
	id BIGINT NOT NULL AUTO_INCREMENT,
    kingdom_name VARCHAR(255) NOT NULL,
    user_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES players(id)
);

CREATE TABLE locations (
	id BIGINT NOT NULL AUTO_INCREMENT,
    x INTEGER,
    y INTEGER,
    kingdom_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (kingdom_id) REFERENCES kingdoms(id)
);

CREATE TABLE buildings (
	id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL,
    level INTEGER NOT NULL,
    hp INTEGER NOT NULL,
    started_at BIGINT,
    finished_at BIGINT,
    under_update BIT NOT NULL,
    kingdom_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (kingdom_id) REFERENCES kingdoms(id)
);

CREATE TABLE resources (
	id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(255) NOT NULL,
    amount INTEGER NOT NULL,
    generation INTEGER NOT NULL,
    updated_at BIGINT,
    kingdom_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (kingdom_id) REFERENCES kingdoms(id)
);

CREATE TABLE troops (
    id BIGINT NOT NULL AUTO_INCREMENT,
    level INTEGER NOT NULL,
    hp INTEGER NOT NULL,
    attack INTEGER NOT NULL,
    defence INTEGER NOT NULL,
    started_at BIGINT,
    finished_at BIGINT,
    kingdom_id BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (kingdom_id) REFERENCES kingdoms(id)
);