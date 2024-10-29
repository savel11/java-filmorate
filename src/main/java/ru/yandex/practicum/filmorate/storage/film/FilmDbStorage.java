package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.FavoriteFilms;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FavoriteFilmsRepository;
import ru.yandex.practicum.filmorate.storage.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.storage.dal.FilmRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final FilmRepository filmRepository;
    private final FavoriteFilmsRepository favoriteFilmsRepository;
    private final FilmGenreRepository filmGenreRepository;

    @Override
    public Collection<Film> getAll() {
        List<Film> filmsFromBd = filmRepository.findAll();
        filmsFromBd.forEach(f -> f.setFilmGenres(getFilmGenresByFilmId(f.getId())));
        return filmsFromBd;
    }

    @Override
    public Film create(Film film) {
        return filmRepository.create(film);
    }

    @Override
    public Film update(Film film) {
        return filmRepository.update(film);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        return filmRepository.findFilmById(id);
    }

    @Override
    public void addFavoriteFilm(Film film, User user) {
        filmRepository.addLikeFilm(film.getId());
        favoriteFilmsRepository.addFavoriteFilm(user.getId(), film.getId());
    }

    @Override
    public Optional<FavoriteFilms> getFavoriteFilmById(Long filmId, Long userId) {
        return favoriteFilmsRepository.findFavoriteFilm(userId, filmId);
    }

    @Override
    public void deleteFavoriteFilm(Film film, User user) {
        filmRepository.deleteLikeFilm(film.getId());
        favoriteFilmsRepository.deleteFavoriteFilm(user.getId(), film.getId());
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        return filmRepository.findPopularsFilm(count);
    }

    @Override
    public List<FilmGenre> getFilmGenresByFilmId(Long filmId) {
        return filmGenreRepository.getAllFilmGenres(filmId);
    }


    @Override
    public boolean deleteFilmById(Long id) {
        return filmRepository.deleteFilmById(id);
    }

    @Override
    public void addFilmGenre(Long filmId, List<Long> genreId) {
        genreId.forEach(el -> filmGenreRepository.addFilmGenre(filmId, el));
    }

    @Override
    public boolean deleteFilmGenre(Long filmId, Long genreId) {
        return filmGenreRepository.deleteFilmGenre(filmId, genreId);
    }

    @Override
    public boolean isDuplicateForUpdated(UpdateFilmDto newFilmDto) {
        return filmRepository.findDuplicateForUpdate(newFilmDto.getName(), newFilmDto.getReleaseDate(),
                newFilmDto.getDuration(), newFilmDto.getId()).isPresent();
    }
}
