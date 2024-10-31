package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.serializers.CustomDurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
public class NewFilmDto {
    @NotBlank(message = "Некорректный формат названия фильма: Название не должно быть пустым.")
    private String name;
    @Size(max = 200, message = "Некорректный формат описания: Описания не должно превышать 200 символов.")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @JsonFormat(pattern = "MINUTES")
    @JsonSerialize(using = CustomDurationSerializer.class)
    private Duration duration;
    private List<Genre> genres;
    private FilmRating mpa;
}
