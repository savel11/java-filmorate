package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
@Slf4j
public class FilmsService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UsersService usersService;

    public Film likeFilm(Long filmId, Long userId) {
        log.trace("Добавляем лайк фильму с id: " + filmId);
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        User user = inMemoryUserStorage.getUserById(userId);
        if (user.getLikedFilms().contains(film.getId())) {
            log.warn("Пользователь " + userId + " уже оценил  фильм " + filmId);
            throw new InvalidFormatException("Лайк уже был поставлен");
        }
        log.trace("Добавляем фильм к списку понравившехся пользователю");
        usersService.addLikeFilm(userId, filmId);
        log.trace("Лайков у фильма " + film.getLikes());
        log.trace("Добвавляем лайк");
        film.setLikes(film.getLikes() + 1);
        log.trace("Лайков у фильма " + film.getLikes());
        return inMemoryFilmStorage.updateCountLikes(film);
    }

    public void deleteLikeFilm(Long filmId, Long userId) {
        log.trace("Удаляем лайк фильму с id: " + filmId);
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        User user = inMemoryUserStorage.getUserById(userId);
        if (!user.getLikedFilms().contains(film.getId())) {
            log.warn("Пользователь " + userId + " не ставил лайк фильму " + filmId);
            throw new InvalidFormatException("Лайка изначально не было");
        }
        log.trace("Удаляем фильм из списку понравившехся пользователю");
        usersService.deleteLikeFilm(userId, filmId);
        log.trace("Лайков у фильма " + film.getLikes());
        log.trace("Удаляем лайк");
        film.setLikes(film.getLikes() - 1);
        log.trace("Лайков у фильма " + film.getLikes());
        inMemoryFilmStorage.updateCountLikes(film);
    }

    public Collection<Film> getPopularFilms(Long count) {
        return inMemoryFilmStorage.getAll().stream().sorted(Comparator.comparing(Film::getLikes).reversed()).limit(count).toList();
    }
}
