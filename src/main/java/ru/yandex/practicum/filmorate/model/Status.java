package ru.yandex.practicum.filmorate.model;

import ru.yandex.practicum.filmorate.exception.NotFoundException;

public enum Status {
    UNCONFIRMED,
    CONFIRMED;

    public static Status getStatus(String str) {
        Status status;
        status = switch (str) {
            case "Unconfirmed" -> UNCONFIRMED;
            case "Confirmed" -> CONFIRMED;
            default -> throw new NotFoundException("Неизвестный статус");
        };
        return status;
    }

    public static String getStatusString(Status status) {
        return switch (status) {
            case Status.CONFIRMED -> "Confirmed";
            case Status.UNCONFIRMED -> "Unconfirmed";
            default -> throw new NotFoundException("Неизвестный статус");
        };
    }
}
