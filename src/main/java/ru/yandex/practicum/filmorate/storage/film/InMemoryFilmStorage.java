package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATA = LocalDate.of(1895, 12, 28);
    private int id = 0;

    private Long generatedId() {
        return (long) ++id;
    }

    private void checkDuplicatedFilmForCreate(Film film) {
        if (films.values().stream().anyMatch(f -> f.getName().equals(film.getName()) &&
                f.getReleaseDate().isEqual(film.getReleaseDate()) && f.getDuration().equals(film.getDuration()))) {
            log.warn("Фильм не был добавлен: Такой фильм уже есть");
            throw new DuplicatedDataException("Фильм уже был добавлен");
        }
    }

    private void checkDuplicatedFilmForUpdate(Film film) {
        if (films.values().stream().filter(f -> !f.equals(film)).anyMatch(f -> f.getName().equals(film.getName()) &&
                f.getReleaseDate().isEqual(film.getReleaseDate()) && f.getDuration().equals(film.getDuration()))) {
            log.warn("Фильм не был добавлен: Такой фильм уже есть");
            throw new DuplicatedDataException("Фильм уже был добавлен");
        }
    }

    private void checkBeforeMinData(LocalDate releaseDate) {
        if (MIN_DATA.isAfter(releaseDate)) {
            log.warn("Фильм не был добавлен: Дата релиза фильма должна быть не раньше 28 декабря 1895 года");
            throw new InvalidFormatException("Некорректный формат даты релиза: Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private void checkNegativeDuration(Duration duration) {
        if (!duration.isPositive()) {
            log.warn("Фильм не был добавлен: Продолжительность не должна быть отрицательным числом");
            throw new InvalidFormatException("Некорректный формат продолжительности фильма: Продолжительность фильма " +
                    "должна быть положителным числом ");
        }
    }

    private void validatedFilmForCreate(Film film) {
        log.trace("Валидация для создания нового фильма: " + film);
        log.trace("Проверка на дубликат");
        checkDuplicatedFilmForCreate(film);
        log.trace("Проверка даты релиза:" + film.getReleaseDate());
        checkBeforeMinData(film.getReleaseDate());
        log.trace("Проверка продолжительности:" + film.getDuration());
        checkNegativeDuration(film.getDuration());
    }

    private void validatedFilmForUpdate(Film film) {
        log.trace("Валидация для обновления фильма: " + film);
        log.trace("Проверка id фильма: " + film.getId());
        if (film.getId() == null) {
            log.warn("Данные не обновленны: Для обновленние нужно указать id фильма");
            throw new InvalidFormatException("Id должен быть указан");
        }
        checkAvailabilityOfFilm(film.getId());
        checkDuplicatedFilmForUpdate(film);
        checkBeforeMinData(film.getReleaseDate());
        checkNegativeDuration(film.getDuration());
    }

    private void checkAvailabilityOfFilm(Long id) {
        if (!films.containsKey(id)) {
            log.warn("Фильм с указанным id не найден");
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        log.info("Добавляем новый фильм");
        log.debug("Фильм: " + film);
        validatedFilmForCreate(film);
        film.setId(generatedId());
        films.put(film.getId(), new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), 0L));
        log.info("Фильм успешно добавлен!");
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Обновляем данные о фильме");
        log.debug("Фильм: " + newFilm);
        validatedFilmForUpdate(newFilm);
        Film oldFilm = films.get(newFilm.getId());
        newFilm.setLikes(oldFilm.getLikes());
        films.put(newFilm.getId(), newFilm);
        log.info("Данные успешно обновленны!");
        return newFilm;
    }

    public Film updateCountLikes(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteAll() {
        films.clear();
        id = 0;
    }

    @Override
    public Film getFilmById(Long id) {
        checkAvailabilityOfFilm(id);
        return new Film(films.get(id).getId(), films.get(id).getName(), films.get(id).getDescription(), films.get(id).getReleaseDate(), films.get(id).getDuration(),
                films.get(id).getLikes());
    }

    @Override
    public void deleteFilmById(Long id) {
        checkAvailabilityOfFilm(id);
        films.remove(id);
    }
}
