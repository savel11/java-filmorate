package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FavoriteFilms;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FavoritesFilmsRowMapper implements RowMapper<FavoriteFilms> {
    @Override
    public FavoriteFilms mapRow(ResultSet rs, int rowNum) throws SQLException {
        FavoriteFilms favoriteFilms = new FavoriteFilms();
        favoriteFilms.setFilmId(rs.getLong("film_id"));
        favoriteFilms.setUserId(rs.getLong("user_id"));
        return favoriteFilms;
    }
}
