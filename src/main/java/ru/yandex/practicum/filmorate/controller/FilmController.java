package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.service.FilmsService;

import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@RequiredArgsConstructor
public class FilmController {
    private final FilmsService filmsService;

    @GetMapping
    public List<FilmDto> getAll() {
        return filmsService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmDto film) {
        return filmsService.createFilm(film);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmDto newFilm) {
        return filmsService.updateFilm(newFilm);
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable Long filmId) {
        return filmsService.getFilmById(filmId);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable Long filmId) {
        filmsService.deleteFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") @Positive int count) {
        return filmsService.getPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public FilmDto likeFilm(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        return filmsService.addFavoriteFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable @Positive Long id, @PathVariable @Positive Long userId) {
        filmsService.deleteFavoriteFilm(id, userId);
    }
}
