package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(of = {"id"})
@RequiredArgsConstructor
@AllArgsConstructor
public class Genre {
    private Long id;
    private String name;
}
