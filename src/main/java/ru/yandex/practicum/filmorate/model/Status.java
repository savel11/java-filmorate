package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

public enum Status {
    Unconfirmed,
    Confirmed;

    public static Status getStatus(String str) {
        Status status;
        status = switch (str) {
            case "Unconfirmed" -> Unconfirmed;
            case "Confirmed" -> Confirmed;
            default -> throw new NotFoundException("Неизвестный статус");
        };
        return status;
    }
}
