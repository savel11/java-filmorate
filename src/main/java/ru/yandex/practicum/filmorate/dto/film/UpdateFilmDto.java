package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.serializers.CustomDurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFilmDto {
    private Long id;
    private String name;
    @Size(max = 200, message = "Некорректный формат описания: Описания не должно превышать 200 символов.")
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;
    @JsonFormat(pattern = "MINUTES")
    @JsonSerialize(using = CustomDurationSerializer.class)
    private Duration duration;
    private List<FilmGenre> genres;
    private int rate;

    public boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return !(releaseDate == null);
    }

    public boolean hasDuration() {
        return !(duration == null);
    }

    public boolean hasFilmGenres() {
        return !(genres == null || genres.isEmpty());
    }

    public boolean hasFilmRating() {
        return !(rate == 0);
    }
}
