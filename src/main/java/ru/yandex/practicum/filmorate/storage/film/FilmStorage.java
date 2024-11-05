package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.FavoriteFilms;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAll();

    Film create(Film film);

    Film update(Film film);

    Optional<Film> getFilmById(Long id);

    void addFavoriteFilm(Film film, User user);

    void deleteFavoriteFilm(Film film, User user);

    Optional<FavoriteFilms> getFavoriteFilmById(Long filmId, Long userId);

    List<Film> getPopularFilm(int count);

    boolean deleteFilmById(Long id);

    void addFilmGenre(Long filmId, List<Long> genreId);

    boolean deleteFilmGenre(Long filmId);
}
