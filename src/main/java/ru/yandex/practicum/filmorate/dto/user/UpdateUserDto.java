package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserDto {
    private Long id;
    @Email(regexp = ".+[@].+[\\.].+", message = "Некорректный формат электронной почты.")
    private String email;
    private String name;
    private String login;
    @PastOrPresent(message = "Некорректная дата рождения: Дата рождения не должна быть в будущем.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;


    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return ! (birthday == null);
    }
}
