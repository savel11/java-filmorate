CREATE TABLE IF NOT EXISTS rating (
 rating_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 rating ENUM('G', 'PG', 'PG-13', 'R', 'NC-17')
);

CREATE TABLE IF NOT EXISTS film (
 film_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 title VARCHAR NOT NULL,
 description VARCHAR,
 release_date DATE,
 duration INTEGER CONSTRAINT positive_duration CHECK (duration > 0),
 count_likes BIGINT CONSTRAINT positive_likes CHECK (count_likes >= 0),
 rating_id  INTEGER REFERENCES rating (rating_id),
 CONSTRAINT not_blank_title CHECK(title != ''),
 CONSTRAINT check_date CHECK(release_date >= '1895-12-28')
);

CREATE TABLE IF NOT EXISTS users (
 user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 email VARCHAR NOT NULL,
 login VARCHAR NOT NULL,
 name VARCHAR NOT NULL,
 birthday DATE
);

CREATE TABLE IF NOT EXISTS friendships (
 user_id1 BIGINT  REFERENCES users (user_id) ON DELETE CASCADE,
 user_id2 BIGINT  REFERENCES users (user_id) ON DELETE CASCADE,
 status ENUM('Unconfirmed', 'Confirmed'),
 UNIQUE (user_id1, user_id2)
);

CREATE TABLE IF NOT EXISTS favorite_films (
 film_id BIGINT  REFERENCES film (film_id) ON DELETE CASCADE,
 user_id BIGINT  REFERENCES users (user_id) ON DELETE CASCADE,
 UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS genre (
 genre_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
 name VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genre (
 film_id BIGINT  REFERENCES film (film_id) ON DELETE CASCADE,
 genre_id BIGINT  REFERENCES genre (genre_id) ON DELETE RESTRICT
);

