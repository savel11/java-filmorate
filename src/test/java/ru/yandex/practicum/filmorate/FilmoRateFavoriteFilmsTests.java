package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.FavoriteFilms;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.FilmoRateFilmStorageTests.createFilmForTest;
import static ru.yandex.practicum.filmorate.FilmoRateUserStorageTests.createUserForTest;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FilmDbStorage.class, FilmRowMapper.class,
        FavoriteFilmsRepository.class, FavoritesFilmsRowMapper.class, FriendshipsRepository.class,
        FriendshipsRowMaapper.class, FilmGenreRepository.class, FilmGenreRowMapper.class})
public class FilmoRateFavoriteFilmsTests {
    private final UserDbStorage userRepository;
    private final FilmDbStorage filmRepository;
    private final FavoriteFilmsRepository favoriteFilmsRepository;

    @Test
    public void testAddFavoriteFilm() {
        Film film = filmRepository.create(createFilmForTest());
        User user = userRepository.create(createUserForTest());
        filmRepository.addFavoriteFilm(film, user);
        Optional<FavoriteFilms> filmOptional = favoriteFilmsRepository.findFavoriteFilm(user.getId(), film.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(fl ->
                        assertThat(fl).hasFieldOrPropertyWithValue("filmId", film.getId())
                );
    }

    @Test
    public void testDeleteFavoriteFilm() {
        Film film = filmRepository.create(createFilmForTest());
        User user = userRepository.create(createUserForTest());
        filmRepository.addFavoriteFilm(film, user);
        filmRepository.deleteFavoriteFilm(film, user);
        Optional<FavoriteFilms> filmOptional = favoriteFilmsRepository.findFavoriteFilm(user.getId(), film.getId());
        assertThat(filmOptional).isEmpty();
    }

    @Test
    public void testFindPopularsFilm() {
        Film film = filmRepository.create(createFilmForTest());
        User user = userRepository.create(createUserForTest());
        filmRepository.addFavoriteFilm(film, user);
        Film newFilm = createFilmForTest();
        newFilm.setName("newFilm");
        filmRepository.create(newFilm);
        List<Film> popular = filmRepository.getPopularFilm(2);
        assertEquals(film.getName(), popular.getFirst().getName(),
                "Неверный популярный фильм");
        assertEquals(2, popular.size(), "Неверное колличесвто фильмов");
    }
}
