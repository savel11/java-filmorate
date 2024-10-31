package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.serializers.CustomRatingSerializer;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FilmRating {
    private int id;
    @JsonSerialize(using = CustomRatingSerializer.class)
    private Rating name;
}
