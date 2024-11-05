package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.serializers.CustomStatusSerializer;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Friendships {
    private Long userId1;
    private Long id;
    @JsonSerialize(using = CustomStatusSerializer.class)
    private Status status;
}
