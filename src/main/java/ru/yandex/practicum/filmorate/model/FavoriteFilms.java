package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FavoriteFilms {
    private Long filmId;
    private Long userId;
}
