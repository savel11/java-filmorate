INSERT INTO  rating(rating)
 SELECT * FROM (VALUES
('G'),
('PG'),
('PG-13'),
('R'),
('NC-17')) AS r (rating)
WHERE NOT EXISTS (SELECT *  FROM rating);

INSERT INTO genre(name)
 SELECT * FROM (VALUES
 ('Комедия'),
 ('Драма'),
 ('Мультфильм'),
 ('Триллер'),
 ('Документальный'),
 ('Боевик')) AS g (name)
 WHERE NOT EXISTS (SELECT * FROM genre);









