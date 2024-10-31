package ru.yandex.practicum.filmorate.storage.film;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FavoriteFilms;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.BaseRepository;
import ru.yandex.practicum.filmorate.storage.dal.FavoriteFilmsRepository;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;

import java.util.List;
import java.util.Optional;


@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private final FavoriteFilmsRepository favoriteFilmsRepository;
    private final FilmGenreRepository filmGenreRepository;
    private static final String FIND_ALL_QUERY = "SELECT f.*, r.rating AS rating, g.genre_id AS genre_id, g.name " +
            " AS genre_name FROM film AS f INNER JOIN rating AS r ON f.rating_id = r.rating_id LEFT OUTER JOIN " +
            "film_genre AS fg ON f.film_id = fg.film_id LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT f.*, r.rating AS rating, g.genre_id AS genre_id, g.name AS " +
            "genre_name FROM film AS f INNER JOIN rating AS r ON f.rating_id = r.rating_id LEFT OUTER JOIN " +
            "film_genre AS fg ON f.film_id = fg.film_id LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id  " +
            "WHERE f.film_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film(title, description, release_date, " +
            "duration, count_likes, rating_id) VALUES (?, ?, ?, ?, 0, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET title = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ?  WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE film_id = ?";
    private static final String FIND_POPULAR_QUERY = "SELECT f.*, r.rating AS rating, g.genre_id AS genre_id, g.name " +
            "AS genre_name FROM film AS f INNER JOIN rating AS r ON f.rating_id = r.rating_id LEFT OUTER JOIN " +
            " film_genre AS fg ON f.film_id = fg.film_id LEFT OUTER JOIN genre AS g ON fg.genre_id = g.genre_id " +
            "ORDER BY count_likes DESC LIMIT ?";
    private static final String ADD_LIKE_FILM_QUERY = "UPDATE film SET count_likes = count_likes + 1 WHERE film_id = ?";
    private static final String DELETE_LIKE_FILM_QUERY = "UPDATE film SET count_likes = count_likes - 1 WHERE film_id = ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, FavoriteFilmsRepository favoriteFilmsRepository,
                         FilmGenreRepository filmGenreRepository) {
        super(jdbc, mapper, Film.class);
        this.favoriteFilmsRepository = favoriteFilmsRepository;
        this.filmGenreRepository = filmGenreRepository;
    }

    @Override
    public List<Film> getAll() {
        List<Film> films = jdbc.query(FIND_ALL_QUERY, new FilmRowMapper());
        return films.stream().distinct().toList();
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        List<Film> films = jdbc.query(FIND_BY_ID_QUERY, new FilmRowMapper(), id);
        if (films.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(films.getFirst());
    }

    @Override
    public Film create(Film film) {
        Long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration().toMinutes(), film.getRating().getId());
        film.setId(id);
        if (film.getFilmGenres() != null) {
            addFilmGenre(film.getId(), film.getFilmGenres().stream().map(Genre::getId).toList());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration().toMinutes(),
                film.getRating().getId(), film.getId());
        return film;
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }

    @Override
    public boolean deleteFilmById(Long id) {
        return delete(DELETE_QUERY, id);
    }

    @Override
    public void addFavoriteFilm(Film film, User user) {
        update(ADD_LIKE_FILM_QUERY, film.getId());
        favoriteFilmsRepository.addFavoriteFilm(user.getId(), film.getId());
    }

    @Override
    public void deleteFavoriteFilm(Film film, User user) {
        update(DELETE_LIKE_FILM_QUERY, film.getId());
        favoriteFilmsRepository.deleteFavoriteFilm(user.getId(), film.getId());
    }

    @Override
    public Optional<FavoriteFilms> getFavoriteFilmById(Long filmId, Long userId) {
        return favoriteFilmsRepository.findFavoriteFilm(userId, filmId);
    }

    @Override
    public void addFilmGenre(Long filmId, List<Long> genreId) {
        genreId.forEach(el -> filmGenreRepository.addFilmGenre(filmId, el));
    }

    @Override
    public boolean deleteFilmGenre(Long filmId) {
        return filmGenreRepository.deleteFilmGenre(filmId);
    }
}
