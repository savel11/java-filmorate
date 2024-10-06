package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/films")
@Slf4j
@Validated
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATA = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавляем новый фильм");
        log.debug("Фильм: " + film);
        if (films.values().stream().anyMatch(f -> f.getName().equals(film.getName()) &&
                f.getReleaseDate().isEqual(film.getReleaseDate()) && f.getDuration().equals(film.getDuration()))) {
            log.warn("Фильм не был добавлен: Такой фильм уже есть");
            throw new DuplicatedDataException("Фильм уже был добавлен");
        }
        if (MIN_DATA.isAfter(film.getReleaseDate())) {
            log.warn("Фильм не был добавлен: Дата релиза фильма должна быть не раньше 28 декабря 1895 года");
            throw new InvalidFormatException("Некорректный формат даты релиза: Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (!film.getDuration().isPositive()) {
            log.warn("Фильм не был добавлен: Продолжительность не должна быть отрицательным числом");
            throw new InvalidFormatException("Некорректный формат продолжительности фильма: Продолжительность фильма " +
                    "должна быть положителным числом ");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен!");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Обновляем данные о фильме");
        log.debug("Фильм: " + newFilm);
        if (newFilm.getId() == null) {
            log.warn("Данные не обновленны: Для обновленние нужно указать id фильма");
            throw new InvalidFormatException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            if (films.values().stream().filter(f -> !f.equals(newFilm)).anyMatch(f -> f.getName().equals(newFilm.getName()) &&
                    f.getReleaseDate().isEqual(newFilm.getReleaseDate()) && f.getDuration().equals(newFilm.getDuration()))) {
                log.warn("Данные не обновленны: Такой фильм уже есть");
                throw new DuplicatedDataException("Фильм уже был добавлен");
            }
            Film oldFilm = films.get(newFilm.getId());
            if (MIN_DATA.isAfter(newFilm.getReleaseDate())) {
                log.warn("Данные не обновленны: Дата релиза должны быть не раньше 28 декабря 1985 года");
                throw new InvalidFormatException("Некорректный формат даты релиза: " +
                        "Дата релиза должна быть не раньше 28 декабря 1895 года");
            }
            if (!newFilm.getDuration().isPositive()) {
                log.warn("Данные не обновленны: Продолжительность должна быть положительным числом");
                throw new InvalidFormatException("Некорректный формат продолжительности фильма: Продолжительность фильма " +
                        "должна быть положителным числом ");
            }
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setName(newFilm.getName());
            log.info("Данные успешно обновленны!");
            return oldFilm;
        }
        log.warn("Данные не обновленны: Фильм с указанным id не найден");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @DeleteMapping
    public void deleteAll() {
        films.clear();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
