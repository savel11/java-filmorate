package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.dal.FavoriteFilmsRepository;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FavoritesFilmsRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class, FavoriteFilmsRepository.class, FilmGenreRepository.class,
        FavoritesFilmsRowMapper.class, FilmGenreRowMapper.class})
public class FilmoRateFilmStorageTests {
    private final FilmDbStorage filmRepository;

    @Test
    public void testCreateFilm() {
        Film film = filmRepository.create(createFilmForTest());
        checkEqualsFilmFields(createFilmForTest(), film);
    }

    @Test
    public void testFindFilmById() {
        Film film = filmRepository.create(createFilmForTest());
        Optional<Film> optionalFilm = filmRepository.getFilmById(film.getId());
        assertThat(optionalFilm)
                .isPresent()
                .hasValueSatisfying(fl ->
                        assertThat(fl).hasFieldOrPropertyWithValue("id", film.getId())
                );
    }

    @Test
    public void testUpdateFilm() {
        Film film = filmRepository.create(createFilmForTest());
        Film newFilm = createFilmForTest();
        newFilm.setName("newName");
        newFilm.setDuration(Duration.ofMinutes(100));
        newFilm.setRating(new FilmRating(2, null));
        newFilm.setId(film.getId());
        newFilm.setReleaseDate(LocalDate.of(2012, 12, 12));
        Film updateFilm = filmRepository.update(newFilm);
        checkEqualsFilmFields(newFilm, updateFilm);
    }

    @Test
    public void testFindAllFilms() {
        filmRepository.create(createFilmForTest());
        Film film = createFilmForTest();
        film.setName("newFilm");
        filmRepository.create(film);
        assertEquals(2, filmRepository.getAll().size(), "Неверное количество фильмов");
    }

    @Test
    public void testDeleteFilm() {
        Film film = filmRepository.create(createFilmForTest());
        filmRepository.deleteFilmById(film.getId());
        Optional<Film> filmOptional = filmRepository.getFilmById(film.getId());
        assertThat(filmOptional).isEmpty();
    }

    public static Film createFilmForTest() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Film for test");
        film.setDuration(Duration.ofMinutes(60));
        film.setReleaseDate(LocalDate.of(2000, 2, 2));
        film.setRating(new FilmRating(1, null));
        return film;
    }

    private void checkEqualsFilmFields(Film film, Film filmFromBd) {
        assertEquals(film.getName(), filmFromBd.getName(), "Неверное название фильма");
        assertEquals(film.getDescription(), filmFromBd.getDescription(), "Неверное описание фильма");
        assertEquals(film.getDuration(), filmFromBd.getDuration(), "Неверная длительность фильма");
        assertEquals(film.getReleaseDate(), filmFromBd.getReleaseDate(), "Неверная дата релиза фильма");
        assertEquals(film.getRating(), filmFromBd.getRating(), "Неверный рейтинг фильма");
    }
}
