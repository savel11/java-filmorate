package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

public enum Rating {
    G,
    PG,
    PG_13,
    R,
    NC_17;

    public static Rating getRating(String str) {
        Rating rating;
        rating = switch (str) {
            case "G" -> Rating.G;
            case "PG" -> Rating.PG;
            case "PG-13", "PG_13" -> Rating.PG_13;
            case "R" -> Rating.R;
            case "NC-17", "NC_17" -> Rating.NC_17;
            default -> throw new NotFoundException("Неизвестный рейтинг фильма");
        };
        return rating;
    }

    public static String getStringRating(Rating rating) {
        return switch (rating) {
            case Rating.G -> "G";
            case Rating.PG -> "PG";
            case Rating.PG_13 -> "PG-13";
            case Rating.R -> "R";
            case Rating.NC_17 -> "NC-17";
            default -> throw new NotFoundException("Неизвестный рейтинг фильма");
        };
    }
}
