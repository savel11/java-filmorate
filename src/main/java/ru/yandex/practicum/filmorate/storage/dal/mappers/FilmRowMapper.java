package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private final Map<Long, Film> films;

    public FilmRowMapper() {
        films = new HashMap<>();
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long id = rs.getLong("film_id");
        Film film = films.get(id);
        if (films.get(id) == null) {
            film = new Film();
            film.setId(id);
            film.setName(rs.getString("title"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(Duration.ofMinutes(rs.getInt("duration")));
            film.setLikes(rs.getLong("count_likes"));
            FilmRating rating = new FilmRating();
            rating.setId(rs.getInt("rating_id"));
            rating.setName(Rating.getRating(rs.getString("rating")));
            film.setRating(rating);
            film.setFilmGenres(new ArrayList<>());
            films.put(id, film);
        }
        Long genreId = rs.getLong("genre_id");
        if (genreId != 0) {
            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(rs.getString("genre_name"));
            film.getFilmGenres().add(genre);
        }
        return film;
    }
}