package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;


import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@RequiredArgsConstructor
public class User {
    private Long id;
    @Email(regexp = ".+[@].+[\\.].+", message = "Некорректный формат электронной почты.")
    @NotBlank(message = "Некорректный формат электронной почты: Электронная почта не должна быть пустой.")
    private String email;
    @NotBlank(message = "Некорректный формат логина: Логин не должен быть пустым.")
    private String login;
    private String name;
    @PastOrPresent(message = "Некорректная дата рождения: Дата рождения не должна быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    private Set<Long> likedFilms;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<User> friends;
}
