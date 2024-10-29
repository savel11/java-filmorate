package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM film";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film(title, description, release_date, " +
            "duration, count_likes, rating_id) VALUES (?, ?, ?, ?, 0, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET title = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ?  WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE film_id = ?";
    private static final String FIND_POPULAR_QUERY = "SELECT * FROM film ORDER BY count_likes DESC LIMIT ?";
    private static final String ADD_LIKE_FILM_QUERY = "UPDATE film SET count_likes = count_likes + 1 WHERE film_id = ?";
    private static final String DELETE_LIKE_FILM_QUERY = "UPDATE film SET count_likes = count_likes - 1 WHERE film_id = ?";

    private static final String FIND_DUPLICATE_FOR_CREATED = "SELECT * FROM film WHERE title = ? AND " +
            "release_date = ? AND duration = ?";
    private static final String FIND_DUPLICATE_FOR_UPDATED = "SELECT * FROM film WHERE title = ? AND " +
            "release_date = ? AND duration = ? AND film_id <> ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findFilmById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Film create(Film film) {
        Long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration().toMinutes(), film.getRatingId());
        film.setId(id);
        return film;
    }

    public Film update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration().toMinutes(),
                film.getRatingId(), film.getId());
        return film;
    }

    public List<Film> findPopularsFilm(int count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }

    public boolean deleteFilmById(Long id) {
        return delete(DELETE_QUERY, id);
    }

    public Optional<Film> addLikeFilm(Long id) {
        update(ADD_LIKE_FILM_QUERY, id);
        return findFilmById(id);
    }

    public Optional<Film> deleteLikeFilm(Long id) {
        update(DELETE_LIKE_FILM_QUERY, id);
        return findFilmById(id);
    }

    public Optional<Film> findDuplicateForCreate(String name, LocalDate date, Duration duration) {
        return findOne(FIND_DUPLICATE_FOR_CREATED, name, date, duration.toMinutes());
    }

    public Optional<Film> findDuplicateForUpdate(String name, LocalDate date, Duration duration, Long id) {
        return findOne(FIND_DUPLICATE_FOR_UPDATED, name, date, duration.toMinutes(), id);
    }
}
