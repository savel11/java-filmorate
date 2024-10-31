package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dal.mappers.RatingRowMapper;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({RatingDbStorage.class, RatingRowMapper.class})
public class FilmoRateRatingStorageTests {
    private final RatingDbStorage ratingDbStorage;

    @Test
    public void testFindRatingById() {
        Optional<FilmRating> filmRating = ratingDbStorage.getFilmRatingById(1);
        assertThat(filmRating).isPresent().hasValueSatisfying(fr -> assertThat(fr)
                .hasFieldOrPropertyWithValue("id", 1));
        assertThat(filmRating).isPresent().hasValueSatisfying(fr -> assertThat(fr)
                .hasFieldOrPropertyWithValue("name", Rating.G));
    }

    @Test
    public void testFindAllRating() {
        assertEquals(5, ratingDbStorage.getAll().size());
    }
}
