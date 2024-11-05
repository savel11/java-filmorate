package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;


import java.time.Duration;
import java.time.LocalDate;
import java.util.List;


@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    private Long id;
    private  String name;
    private  String description;
    private  LocalDate releaseDate;
    private  Duration duration;
    private Long likes;
    private List<Genre> filmGenres;
    private FilmRating rating;
}

