package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.dal.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("RatingDbStorage")
public class RatingDbStorage extends BaseRepository<FilmRating> implements RatingStorage {
    private static final String GET_RATING_QUERY = "SELECT * FROM rating WHERE rating_id = ?";
    private static final String GET_ALL_RATING_QUERY = "SELECT * FROM rating";

    public RatingDbStorage(JdbcTemplate jdbc, RowMapper<FilmRating> mapper) {
        super(jdbc, mapper, FilmRating.class);
    }

    @Override
    public Optional<FilmRating> getFilmRatingById(int id) {
        return findOne(GET_RATING_QUERY, id);
    }

    @Override
    public List<FilmRating> getAll() {
        return findMany(GET_ALL_RATING_QUERY);
    }
}
