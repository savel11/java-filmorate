package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.FavoriteFilms;


import java.util.Optional;


@Repository
public class FavoriteFilmsRepository extends BaseRepository<FavoriteFilms> {

    private static final String ADD_FILM_QUERY = "INSERT INTO favorite_films(film_id, user_id) VALUES (?, ?)";
    private static final String DELETED_FILM_QUERY = "DELETE FROM favorite_films WHERE film_id = ? AND user_id = ?";
    private static final String FIND_FAVORITE_FILM = "SELECT * FROM favorite_films WHERE film_id = ? AND user_id = ?";

    public FavoriteFilmsRepository(JdbcTemplate jdbc, RowMapper<FavoriteFilms> mapper) {
        super(jdbc, mapper, FavoriteFilms.class);
    }

    public void addFavoriteFilm(Long userId, Long filmId) {
        int rowSave = jdbc.update(ADD_FILM_QUERY, filmId, userId);
        if (rowSave == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    public boolean deleteFavoriteFilm(Long userId, Long filmId) {
        return delete(DELETED_FILM_QUERY, filmId, userId);
    }

    public Optional<FavoriteFilms> findFavoriteFilm(Long userId, Long filmId) {
        return findOne(FIND_FAVORITE_FILM, filmId, userId);
    }
}
