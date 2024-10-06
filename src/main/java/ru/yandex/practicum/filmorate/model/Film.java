package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.time.Duration;
import java.time.LocalDate;


@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Long id;
    @NotBlank(message = "Некорректный формат названия фильма: Название не должно быть пустым.")
    private String name;
    @Size(max = 200, message = "Некорректный формат описания: Описания не должно превышать 200 символов.")
    private String description;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate releaseDate;
    @JsonFormat(pattern = "MINUTES")
    private Duration duration;
}
