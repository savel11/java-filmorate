INSERT INTO rating(rating)
SELECT 'G'
WHERE NOT EXISTS (SELECT rating FROM rating WHERE rating = 'G');

INSERT INTO rating(rating)
SELECT 'PG'
WHERE NOT EXISTS (SELECT rating FROM rating WHERE rating = 'PG');

INSERT INTO rating(rating)
SELECT 'PG-13'
WHERE NOT EXISTS (SELECT rating FROM rating WHERE rating = 'PG-13');

INSERT INTO rating(rating)
SELECT 'R'
WHERE NOT EXISTS (SELECT rating FROM rating WHERE rating = 'R');

INSERT INTO rating(rating)
SELECT 'NC-17'
WHERE NOT EXISTS (SELECT rating FROM rating WHERE rating = 'NC-17');

INSERT INTO genre(name)
SELECT 'Комедия'
WHERE NOT EXISTS (SELECT name FROM genre WHERE name = 'Комедия');

INSERT INTO genre(name)
SELECT 'Драма'
WHERE NOT EXISTS (SELECT name FROM genre WHERE name = 'Драма');

INSERT INTO genre(name)
SELECT 'Мультфильм'
WHERE NOT EXISTS (SELECT name FROM genre WHERE name = 'Мультфильм');

INSERT INTO genre(name)
SELECT 'Триллер'
WHERE NOT EXISTS (SELECT name FROM genre WHERE name = 'Триллер');

INSERT INTO genre(name)
SELECT 'Документальный'
WHERE NOT EXISTS (SELECT name FROM genre WHERE name = 'Документальный');

INSERT INTO genre(name)
SELECT 'Боевик'
WHERE NOT EXISTS (SELECT name FROM genre WHERE name = 'Боевик');







