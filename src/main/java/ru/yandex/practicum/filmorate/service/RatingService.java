package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.List;

@Service
public class RatingService {
    @Autowired
    @Qualifier("RatingDbStorage")
    private RatingStorage ratingStorage;

    public FilmRating getRatingById(int id) {
        if (ratingStorage.getFilmRatingById(id).isEmpty()) {
            throw new NotFoundException("Рейтинга с id " + id + " не существует");
        }
        return ratingStorage.getFilmRatingById(id).get();
    }

    public List<FilmRating> getAll() {
        return ratingStorage.getAll().stream().toList();
    }
}
