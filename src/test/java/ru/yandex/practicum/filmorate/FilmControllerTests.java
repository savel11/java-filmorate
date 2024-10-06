package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;


import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class FilmControllerTests {
    Film film;
    @Autowired
    private FilmController filmController;

    @BeforeEach
    public void createTestFilm() {
        film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setDuration(Duration.ofMinutes(120));
        film.setReleaseDate(LocalDate.of(2000, 5, 21));
    }

    @BeforeEach
    public void clearFilms() {
        filmController.deleteAll();
    }

    @Test
    void createFilmWithoutName() {
        film.setName("");
        assertThrows(ConstraintViolationException.class, () -> filmController.create(film),
                "Добавление фильма с пустым названием должно приводить к ошибки валидации");
        film.setName(null);
        assertThrows(ConstraintViolationException.class, () -> filmController.create(film),
                "Добавление фильма без названия  названием должно приводить к ошибки валидации");
    }

    @Test
    void createFilmWithNotCorrectDescription() {
        film.setDescription("a".repeat(201));
        assertThrows(ConstraintViolationException.class, () -> filmController.create(film),
                "Добавление фильма с описанием более чем в 200 символов должно приводить к ошибки валидации");
    }

    @Test
    void createFilmWithNotCorrectReleaseDate() {
        film.setReleaseDate(LocalDate.of(1000, 5, 21));
        assertThrows(InvalidFormatException.class, () -> filmController.create(film),
                "Добавление фильма с датой релиза раньше чем 28 декабря 1895 года должна приводить к ошибки валидации");
    }

    @Test
    void createFilmWithNotCorrectDuration() {
        film.setDuration(Duration.ofMinutes(-120));
        assertThrows(InvalidFormatException.class, () -> filmController.create(film),
                "Добавление фильма с отрицательной длительностью должна приводить к ошибки валидации");
        film.setDuration(Duration.ofMinutes(0));
        assertThrows(InvalidFormatException.class, () -> filmController.create(film),
                "Добавление фильма с нулевой длительностью должна приводить к ошибки валидации");
    }

    @Test
    void createDuplicatedFilm() {
        filmController.create(film);
        assertThrows(DuplicatedDataException.class, () -> filmController.create(film),
                "Добавление дубликата фильма должно приводить к ошибки валидации");
    }

    @Test
    void createFilmWithCorrectData() {
        filmController.create(film);
    }

    @Test
    void createFilmWithBoundaryConditions() {
        film.setDescription("a".repeat(200));
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        filmController.create(film);
    }

    @Test
    void getFilms() {
        film.setName("Seven");
        filmController.create(film);
        assertFalse(filmController.getAll().isEmpty());
    }

    @Test
    void updateFilmWithoutId() {
        assertThrows(InvalidFormatException.class, () -> filmController.update(film),
                "Обновление фильма без указания id  должно приводить к ошибки валидации");
    }

    @Test
    void updateFilmWithNotCorrectId() {
        film.setId(250L);
        assertThrows(NotFoundException.class, () -> filmController.update(film),
                "Обновление фильма который не был добавлен  должно приводить к ошибки");
    }

    @Test
    void updateFilmOnDuplicated() {
        film.setName("One");
        filmController.create(film);
        Film film1 = new Film();
        film1.setName("Two");
        film1.setDuration(film.getDuration());
        film1.setReleaseDate(film.getReleaseDate());
        filmController.create(film1);
        film1.setName("One");
        film1.setId(2L);
        assertThrows(DuplicatedDataException.class, () -> filmController.update(film),
                "Обновление фильма, которое приводит к возникновению дубликата, должна приводить к ошибки");
    }

    @Test
    void updateFilmOnNotCorrectName() {
        filmController.create(film);
        film.setName("");
        film.setId(1L);
        assertThrows(ConstraintViolationException.class, () -> filmController.update(film),
                "Изменение названия фильма на пустое должно приводить к ошибки валидации");
        film.setName(null);
        assertThrows(ConstraintViolationException.class, () -> filmController.update(film),
                "Изменение названия фильма на null должно приводить к ошибки валидации");
    }

    @Test
    void updateFilmOnNotCorrectDescription() {
        filmController.create(film);
        film.setDescription("a".repeat(201));
        assertThrows(ConstraintViolationException.class, () -> filmController.update(film),
                "Изменение описание фильма на описание длиной более 200 символов " +
                        " должно приводить к ошибки валидации");
    }

    @Test
    void updateFilmOnNotCorrectDuration() {
        filmController.create(film);
        film.setId(1L);
        film.setDuration(Duration.ofMinutes(-120));
        assertThrows(InvalidFormatException.class, () -> filmController.update(film),
                "Обновление длительности фильма на отрицательную длительность должно приводить к ошибки валидации");
        film.setDuration(Duration.ofMinutes(0));
        assertThrows(InvalidFormatException.class, () -> filmController.update(film),
                "Обновление длительности фильма на нулевую длительность должно приводить к ошибки валидации");
    }

    @Test
    void updateFilmOnNotCorrectRealiseDate() {
        filmController.create(film);
        film.setId(1L);
        film.setReleaseDate(LocalDate.of(1000, 2, 1));
        assertThrows(InvalidFormatException.class, () -> filmController.update(film),
                "Обновление даты релиза фильма на дату раньше чем 28 декабря 1895 года должно приводить к ошибки валидации");
    }

    @Test
    void updateFilm() {
        filmController.create(film);
        film.setName("NewFilm");
        assertEquals("NewFilm", filmController.update(film).getName(), "Данные не обновились");
    }
}
