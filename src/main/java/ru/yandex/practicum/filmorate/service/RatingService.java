package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;
import java.util.Optional;

@Service
public class RatingService {
    @Autowired
    @Qualifier("RatingDbStorage")
    private RatingStorage ratingStorage;

    public FilmRating getRatingById(int id) {
        Optional<FilmRating> filmRating = ratingStorage.getFilmRatingById(id);
        if (filmRating.isEmpty()) {
            throw new NotFoundException("Рейтинга с id " + id + " не существует");
        }
        return filmRating.get();
    }

    public List<FilmRating> getAll() {
        return ratingStorage.getAll().stream().toList();
    }
}
