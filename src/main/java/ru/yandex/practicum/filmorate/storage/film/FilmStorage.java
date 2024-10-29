package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.*;

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

    List<FilmGenre> getFilmGenresByFilmId(Long filmId);

    boolean deleteFilmById(Long id);

    void addFilmGenre(Long filmId, List<Long> genreId);

    boolean deleteFilmGenre(Long filmId, Long genreId);

    boolean isDuplicateForUpdated(UpdateFilmDto newFilmDto);
}
