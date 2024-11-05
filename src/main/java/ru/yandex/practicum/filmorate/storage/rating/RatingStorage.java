package ru.yandex.practicum.filmorate.storage.rating;

import ru.yandex.practicum.filmorate.model.FilmRating;

import java.util.Collection;
import java.util.Optional;


public interface RatingStorage {
    Collection<FilmRating> getAll();

    Optional<FilmRating> getFilmRatingById(int id);
}
