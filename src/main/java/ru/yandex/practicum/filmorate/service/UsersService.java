package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;

    public User addFriend(Long adderId, Long addedId) {
        log.trace("Добавляем пользователя в друзья");
        User adderUser = inMemoryUserStorage.getUserById(adderId);
        User addedUser = inMemoryUserStorage.getUserById(addedId);
        log.trace("Проверяем нету ли добавляемого пользователя в друзья");
        if (adderUser.getFriends().contains(addedUser)) {
            log.warn("Пользователь уже добавлен в друзья");
            throw new DuplicatedDataException("Пользователь уже есть в друзьях");
        }
        log.trace("Перед добавлением у пользователя с id:" + addedId + " друзей " + addedUser.getFriends().size());
        log.trace("Перед добавлением у пользователя с id:" + adderId + " друзей " + adderUser.getFriends().size());
        addedUser.getFriends().add(adderUser);
        adderUser.getFriends().add(addedUser);
        log.trace("После добавления у пользователя с id:" + addedId + " друзей " + addedUser.getFriends().size());
        log.trace("Перед добавления у пользователя с id:" + adderId + " друзей " + adderUser.getFriends().size());
        inMemoryUserStorage.updateFriends(addedUser, adderUser);
        return adderUser;
    }

    public void deleteFriend(Long adderId, Long addedId) {
        log.trace("Удаляем из друзей пользователя");
        User adderUser = inMemoryUserStorage.getUserById(adderId);
        User addedUser = inMemoryUserStorage.getUserById(addedId);
        log.trace("Перед удалением у пользователя с id:" + addedId + " друзей " + addedUser.getFriends().size());
        log.trace("Перед удалением у пользователя с id:" + adderId + " друзей " + adderUser.getFriends().size());
        addedUser.getFriends().remove(adderUser);
        adderUser.getFriends().remove(addedUser);
        log.trace("После удаления у пользователя с id:" + addedId + " друзей " + addedUser.getFriends().size());
        log.trace("Перед удаления у пользователя с id:" + adderId + " друзей " + adderUser.getFriends().size());
        inMemoryUserStorage.updateFriends(addedUser, adderUser);
    }

    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        User user = inMemoryUserStorage.getUserById(userId);
        User friend = inMemoryUserStorage.getUserById(friendId);
        if (user.getFriends() == null || friend.getFriends() == null) {
            return Collections.emptySet();
        }
        Set<User> commonFriends = new HashSet<>(user.getFriends());
        if (commonFriends.retainAll(friend.getFriends())) {
            return commonFriends;
        }
        return Collections.emptySet();
    }

    public Collection<User> getFriends(Long userId) {
        User user = inMemoryUserStorage.getUserById(userId);
        return user.getFriends();
    }

    public void addLikeFilm(Long userId, Long filmId) {
        log.trace("Добавляем понравившейся фильм пользователю: " + userId);
        User user = inMemoryUserStorage.getUserById(userId);
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        log.trace("Перед добвалением понравившехся фильмов" + user.getLikedFilms().size());
        user.getLikedFilms().add(film.getId());
        log.trace("После добавления" + user.getLikedFilms().size());
        inMemoryUserStorage.updateLikesFilms(user);
    }

    public void deleteLikeFilm(Long userId, Long filmId) {
        log.trace("Удаляем фильм из понравившехся фильмов пользователю: " + userId);
        User user = inMemoryUserStorage.getUserById(userId);
        Film film = inMemoryFilmStorage.getFilmById(filmId);
        log.trace("Перед удалением понравившехся фильмов" + user.getLikedFilms().size());
        user.getLikedFilms().remove(film.getId());
        log.trace("После удаления" + user.getLikedFilms().size());
        inMemoryUserStorage.updateLikesFilms(user);
    }
}
