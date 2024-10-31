package ru.yandex.practicum.filmorate.storage.rating;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("InMemoryRatingStorage")
public class InMemoryRatingStorage implements RatingStorage {
    private final Map<Integer, FilmRating> ratings = Map.of(1, new FilmRating(1, Rating.G), 2, new
            FilmRating(2, Rating.PG), 3, new FilmRating(3, Rating.PG_13), 4, new FilmRating(
            4, Rating.R), 5, new FilmRating(5, Rating.R), 6, new FilmRating(6,
            Rating.NC_17));

    @Override
    public Collection<FilmRating> getAll() {
        return ratings.values();
    }

    @Override
    public Optional<FilmRating> getFilmRatingById(int id) {
        if (ratings.containsKey(id)) {
            return Optional.of(ratings.get(id));
        }
        return Optional.empty();
    }
}
