package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<FilmRating> {
    @Override
    public FilmRating mapRow(ResultSet rs, int rowNum) throws SQLException {
        FilmRating filmRating = new FilmRating();
        filmRating.setId(rs.getInt("rating_id"));
        filmRating.setName(Rating.getRating(rs.getString("rating")));
        return filmRating;
    }
}
