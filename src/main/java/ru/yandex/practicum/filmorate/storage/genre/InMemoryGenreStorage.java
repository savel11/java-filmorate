package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("InMemoryGenreStorage")
public class InMemoryGenreStorage implements GenreStorage {
    private final Map<Long, Genre> genres = new HashMap<>();
    private Long id = 0L;

    private Long generatedId() {
        return ++id;
    }

    @Override
    public List<Genre> getAll() {
        return genres.values().stream().toList();
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        if (genres.containsKey(id)) {
            return Optional.of(genres.get(id));
        }
        return Optional.empty();
    }

    @Override
    public Genre addGenre(Genre genre) {
        genre.setId(generatedId());
        genres.put(genre.getId(), genre);
        return genre;
    }

    @Override
    public boolean deleteGenre(Long id) {
        return genres.remove(id) != null;
    }

    @Override
    public Genre update(Genre genre) {
        genres.put(genre.getId(), genre);
        return genre;
    }
}
