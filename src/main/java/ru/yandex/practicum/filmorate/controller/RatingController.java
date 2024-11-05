package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.service.RatingService;


import java.util.List;

@RestController
@RequestMapping("/mpa")
@Validated
@RequiredArgsConstructor
public class RatingController {
    private final RatingService ratingService;

    @GetMapping
    public List<FilmRating> getAll() {
        return ratingService.getAll();
    }

    @GetMapping("/{id}")
    public FilmRating getGRatingById(@PathVariable int id) {
        return ratingService.getRatingById(id);
    }
}
