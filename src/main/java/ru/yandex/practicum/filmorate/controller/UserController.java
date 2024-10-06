package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создаем нового пользователя");
        log.debug("Пользоваетль: " + user);
        if (user.getLogin().contains(" ")) {
            log.warn("Пользователь не был создан: Логин пользователя не должен содержать пробелов");
            throw new InvalidFormatException("Некорректный формат логина: Логин не должен содержать пробелов.");
        }
        if (users.values().stream().map(User::getEmail).anyMatch(email -> user.getEmail().equals(email))) {
            log.warn("Пользователь не был создан: Пользователь с таким email уже существует");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан!");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Обновление данных пользователя");
        log.debug("Пользоваетль: " + newUser);
        if (newUser.getId() == null) {
            log.warn("Данные не обновлены: Для обновление нужно указать id пользователя");
            throw new InvalidFormatException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getLogin().contains(" ")) {
                log.warn("Данные не были обновленны: Логин пользователя не должен содержать пробелов");
                throw new InvalidFormatException("Некорректный формат логина: Логин не должен содержать пробелов.");
            }
            if (users.values().stream().filter(us -> !us.equals(newUser)).map(User::getEmail)
                    .anyMatch(email -> email.equals(newUser.getEmail()))) {
                log.warn("Данные не были обновленны: Пользователь с таким email уже существует");
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            oldUser.setLogin(newUser.getLogin());
            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());
            if ((newUser.getName() == null || newUser.getName().isBlank())) {
                oldUser.setName(newUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }
            log.info("Данные пользователя успешно обновленны");
            return oldUser;
        }
        log.warn("Данные не обновлены: Пользователя с id = " + newUser.getId() + " не существует");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @DeleteMapping
    public void deleteAll() {
        users.clear();
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
