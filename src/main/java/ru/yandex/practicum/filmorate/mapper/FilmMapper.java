package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    public static Film mapToFilm(NewFilmDto filmDto) {
        Film film = new Film();
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setDuration(filmDto.getDuration());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setFilmGenres(filmDto.getGenres());
        film.setRatingId(filmDto.getMpa().getId());
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setDuration(film.getDuration());
        filmDto.setReleaseDate(film.getReleaseDate());
        if (film.getFilmGenres() != null && !film.getFilmGenres().isEmpty()) {
            filmDto.setGenres(film.getFilmGenres().stream().map(g -> new Genre(g.getId(), null)).toList());
        }
        filmDto.setLikes(filmDto.getLikes());
        FilmRating filmRating = new FilmRating();
        filmRating.setId(film.getRatingId());
        filmDto.setMpa(filmRating);
        return filmDto;
    }
}
