package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmsService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;


import java.util.Collection;


@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
public class FilmController {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final FilmsService filmsService;

    @GetMapping
    public Collection<Film> getAll() {
        return inMemoryFilmStorage.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return inMemoryFilmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        return inMemoryFilmStorage.update(newFilm);
    }

    @DeleteMapping
    public void deleteAll() {
        inMemoryFilmStorage.deleteAll();
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable Long filmId) {
        return inMemoryFilmStorage.getFilmById(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) {
        inMemoryFilmStorage.deleteFilmById(filmId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam("count") @Positive Long count) {
        return filmsService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film likeFilm(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        return filmsService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        filmsService.deleteLikeFilm(id, userId);
    }
}
