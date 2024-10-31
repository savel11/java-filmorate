package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FavoriteFilms;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("InMemoryFilmStorage")
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final Map<Long, Film> films = new HashMap<>();
    private long id = 0;

    private Long generatedId() {
        return ++id;
    }



    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film create(Film film) {
        film.setId(generatedId());
        films.put(film.getId(), new Film(film.getId(), film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), 0L, new ArrayList<>(), film.getRating()));
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        newFilm.setLikes(oldFilm.getLikes());
        films.put(newFilm.getId(), newFilm);
        return newFilm;
    }

    public void updateCountLikes(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        if (films.containsKey(id)) {
            return Optional.of(new Film(films.get(id).getId(), films.get(id).getName(),
                    films.get(id).getDescription(), films.get(id).getReleaseDate(), films.get(id).getDuration(),
                    films.get(id).getLikes(), films.get(id).getFilmGenres(), films.get(id).getRating()));
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteFilmById(Long id) {
        return films.remove(id) != null;
    }

   @Override
    public void addFilmGenre(Long filmId, List<Long> genreId) {
        genreId.forEach(el -> films.get(filmId).getFilmGenres().add(new Genre(el, null)));
    }

    @Override
    public boolean deleteFilmGenre(Long filmId) {
        films.get(filmId).getFilmGenres().clear();
        return films.get(filmId).getFilmGenres().isEmpty();
    }

    @Override
    public void addFavoriteFilm(Film film, User user) {
        FavoriteFilms favoriteFilms = new FavoriteFilms();
        favoriteFilms.setFilmId(film.getId());
        favoriteFilms.setUserId(user.getId());
        user.getLikedFilms().add(favoriteFilms);
        inMemoryUserStorage.updateLikesFilms(user);
        film.setLikes(film.getLikes() + 1);
        updateCountLikes(film);
    }

    @Override
    public void deleteFavoriteFilm(Film film, User user) {
        FavoriteFilms favoriteFilms = new FavoriteFilms();
        favoriteFilms.setFilmId(film.getId());
        favoriteFilms.setUserId(user.getId());
        user.getLikedFilms().remove(favoriteFilms);
        film.setLikes(film.getLikes() - 1);
        inMemoryUserStorage.updateLikesFilms(user);
        updateCountLikes(film);
    }


    @Override
    public Optional<FavoriteFilms> getFavoriteFilmById(Long filmId, Long userId) {
        return inMemoryUserStorage.getUserById(userId).get().getLikedFilms().stream().filter(f -> f.getFilmId()
                .equals(filmId) && f.getUserId().equals(userId)).findFirst();
    }

    @Override
    public List<Film> getPopularFilm(int count) {
        return films.values().stream().sorted(Comparator.comparing(Film::getLikes).reversed()).limit(count).toList();
    }
}
