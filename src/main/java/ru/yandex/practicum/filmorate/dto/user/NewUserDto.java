package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserDto {
    @Email(regexp = ".+[@].+[\\.].+", message = "Некорректный формат электронной почты.")
    @NotBlank(message = "Некорректный формат электронной почты: Электронная почта не должна быть пустой.")
    private String email;
    private String name;
    @NotBlank(message = "Некорректный формат логина: Логин не должен быть пустым.")
    private String login;
    @PastOrPresent(message = "Некорректная дата рождения: Дата рождения не должна быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
}
