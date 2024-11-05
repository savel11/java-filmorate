package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmGenreRepository extends BaseRepository<FilmGenre> {
    private static final String INSERT_QUERY = "INSERT INTO film_genre(film_id, genre_id) VALUES(?, ?)";
    private static final String FIND_FILM_GENRE_BY_ID_QUERY = "SELECT * from film_genre WHERE film_id = ? AND " +
            "genre_id = ?";
    private static final String FIND_FILM_ALL_GENRE_BY_ID_QUERY = "SELECT * from film_genre WHERE film_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM film_genre WHERE film_id = ?";

    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<FilmGenre> mapper) {
        super(jdbc, mapper, FilmGenre.class);
    }

    public void addFilmGenre(Long filmId, Long genreId) {
        int rowSave = jdbc.update(INSERT_QUERY, filmId, genreId);
        if (rowSave == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    public Optional<FilmGenre> getFilmGenreByID(Long filmId, Long genreId) {
        return findOne(FIND_FILM_GENRE_BY_ID_QUERY, filmId, genreId);
    }

    public List<FilmGenre> getAllFilmGenres(Long filmId) {
        return findMany(FIND_FILM_ALL_GENRE_BY_ID_QUERY, filmId);
    }

    public boolean deleteFilmGenre(Long filmId) {
        return delete(DELETE_QUERY, filmId);
    }
}
