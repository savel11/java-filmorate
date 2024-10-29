package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.serializers.CustomDurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    private Long id;
    private  String name;
    private  String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @JsonFormat(pattern = "MINUTES")
    @JsonSerialize(using = CustomDurationSerializer.class)
    private Duration duration;
    private Long likes;
    private List<Genre> genres;
    private FilmRating mpa;
}
