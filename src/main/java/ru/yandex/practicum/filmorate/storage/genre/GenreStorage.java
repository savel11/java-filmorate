package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;


import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    List<Genre> getAll();

    Optional<Genre> getGenreById(Long id);

    Genre addGenre(Genre genre);

    boolean deleteGenre(Long id);

    Genre update(Genre genre);
}
