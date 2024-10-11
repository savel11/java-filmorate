package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.resources.CustomDurationSerializer;


import java.time.Duration;
import java.time.LocalDate;


@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@RequiredArgsConstructor
public class Film {
    private Long id;
    @NotBlank(message = "Некорректный формат названия фильма: Название не должно быть пустым.")
    private  String name;
    @Size(max = 200, message = "Некорректный формат описания: Описания не должно превышать 200 символов.")
    private  String description;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private  LocalDate releaseDate;
    @JsonFormat(pattern = "MINUTES")
    @JsonSerialize(using = CustomDurationSerializer.class)
    private  Duration duration;
    private Long likes;
}

