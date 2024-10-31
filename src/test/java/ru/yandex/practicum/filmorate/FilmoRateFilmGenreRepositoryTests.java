package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.storage.dal.FavoriteFilmsRepository;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.FriendshipsRepository;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FavoritesFilmsRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmGenreRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendshipsRowMaapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.FilmoRateFilmStorageTests.createFilmForTest;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmGenreRepository.class, FilmGenreRowMapper.class, UserDbStorage.class, UserRowMapper.class,
        FriendshipsRepository.class, FriendshipsRowMaapper.class, FilmDbStorage.class, FilmRowMapper.class,
        FavoriteFilmsRepository.class, FavoritesFilmsRowMapper.class,})
public class FilmoRateFilmGenreRepositoryTests {
    private final FilmGenreRepository filmGenreRepository;
    private final FilmDbStorage filmRepository;

    @Test
    public void testAddFilmGenre() {
        Film film = filmRepository.create(createFilmForTest());
        filmGenreRepository.addFilmGenre(film.getId(), 1L);
        Optional<FilmGenre> filmGenreOptional = filmGenreRepository.getFilmGenreByID(film.getId(), 1L);
        assertThat(filmGenreOptional).isPresent().hasValueSatisfying(fg -> assertThat(fg)
                .hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testFindAllFilmGenres() {
        Film film = filmRepository.create(createFilmForTest());
        filmGenreRepository.addFilmGenre(film.getId(), 1L);
        filmGenreRepository.addFilmGenre(film.getId(), 2L);
        assertEquals(2, filmGenreRepository.getAllFilmGenres(film.getId()).size(),
                "Неверное количество жанров");
    }

    @Test
    public void testDeleteFilmGenre() { // Переделать
        Film film = filmRepository.create(createFilmForTest());
        filmGenreRepository.addFilmGenre(film.getId(), 1L);
        filmGenreRepository.addFilmGenre(film.getId(), 2L);
        filmGenreRepository.deleteFilmGenre(film.getId());
        assertEquals(0, filmGenreRepository.getAllFilmGenres(film.getId()).size(),
                "Неверное количество жанров");
    }
}
