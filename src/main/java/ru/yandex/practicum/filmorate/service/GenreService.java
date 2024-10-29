package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.NewGenreDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
public class GenreService {
    @Autowired
    @Qualifier("GenreDbStorage")
    private GenreStorage genreStorage;

    public Genre getGenreById(Long id) {
        if (genreStorage.getGenreById(id).isEmpty()) {
            throw new NotFoundException("Жарн с id = " + id + " не найден");
        }
        return genreStorage.getGenreById(id).get();
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre createGenre(NewGenreDto genre) {
        if (genreStorage.getAll().stream().map(Genre::getName).anyMatch(name -> name.equals(genre.getName()))) {
            throw new DuplicatedDataException("Жанр с таким названием уже существует");
        }
        return genreStorage.addGenre(GenreMapper.mapToGenre(genre));
    }

    public void deleteGenre(Long id) {
        if (genreStorage.getGenreById(id).isEmpty()) {
            throw new NotFoundException("Жарн с id = " + id + " не найден");
        }
        genreStorage.deleteGenre(id);
    }

    public Genre updateGenre(Genre genre) {
        if (genre.getId() == null) {
            throw new ValidationException("Необходимо указать id");
        }
        if (genreStorage.getGenreById(genre.getId()).isEmpty()) {
            throw new NotFoundException("Жарн с id = " + genre.getId() + " не найден");
        }
        return genreStorage.update(genre);
    }
}
