package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.NewGenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreMapper {
    public static Genre mapToGenre(NewGenreDto newGenreDto) {
        Genre genre = new Genre();
        genre.setName(newGenreDto.getName());
        return genre;
    }
}
