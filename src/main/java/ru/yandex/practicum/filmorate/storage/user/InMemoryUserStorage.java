package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    private Long generatedId() {
        return (long) ++id;
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User create(User newUser) {
        log.info("Создаем нового пользователя");
        log.debug("Пользоваетль: " + newUser);
        User user = validateUserForCreate(newUser);
        user.setId(generatedId());
        users.put(user.getId(), new User(user.getId(), user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), new HashSet<Long>(), new HashSet<User>()));
        log.info("Пользователь успешно создан!");
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление данных пользователя");
        log.debug("Пользоваетль: " + newUser);
        User oldUser = users.get(newUser.getId());
        User user = validateUserForUpdate(newUser);
        user.setFriends(oldUser.getFriends());
        user.setLikedFilms(oldUser.getLikedFilms());
        users.put(user.getId(), new User(user.getId(), user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getLikedFilms(), user.getFriends()));
        return user;
    }

    @Override
    public void deleteAll() {
        users.clear();
        id = 0L;
    }

    @Override
    public User getUserById(Long id) {
        checkAvailabilityOfUser(id);
        User user = users.get(id);
        return new User(user.getId(), user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getLikedFilms(), user.getFriends());
    }

    @Override
    public void deleteUserById(Long id) {
        checkAvailabilityOfUser(id);
        users.remove(id);
    }

    public void updateFriends(User user, User newUser) {
        users.put(user.getId(), user);
        users.put(newUser.getId(), newUser);
    }

    public void updateLikesFilms(User user) {
        users.put(user.getId(), user);
    }

    private void checkLogin(String login) {
        if (login.contains(" ")) {
            log.warn("Пользователь не был создан: Логин пользователя не должен содержать пробелов");
            throw new InvalidFormatException("Некорректный формат логина: Логин не должен содержать пробелов.");
        }
    }

    private void checkDuplicatedEmailForCreated(String email) {
        if (users.values().stream().map(User::getEmail).anyMatch(e -> e.equals(email))) {
            log.warn("Пользователь не был создан: Пользователь с таким email уже существует");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    private void checkDuplicatedEmailForUpdate(User user) {
        if (users.values().stream().filter(u -> !u.equals(user)).map(User::getEmail).anyMatch(e -> e.equals(user.getEmail()))) {
            log.warn("Данные не были обновленны: Пользователь с таким email уже существует");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
    }

    private User validatedName(User user) {
        log.trace("Проверка имени пользователя: " + user.getName());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.trace("Имя пользователя изменяем на его логин: " + user.getName());
            return user;
        }
        return user;
    }

    private void checkAvailabilityOfUser(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Данные не обновлены: Пользователя с id = " + id + " не существует");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    private User validateUserForCreate(User user) {
        log.trace("Валидация для создания нового пользователя: " + user);
        log.trace("Проверка корректности логина: " + user.getLogin());
        checkLogin(user.getLogin());
        log.trace("Проверка на дубликат email: " + user.getEmail());
        checkDuplicatedEmailForCreated(user.getEmail());
        return validatedName(user);
    }

    private User validateUserForUpdate(User user) {
        log.trace("Валидация для обновления  пользователя:" + user);
        if (user.getId() == null) {
            log.warn("Данные не обновлены: Для обновление нужно указать id пользователя");
            throw new InvalidFormatException("Id должен быть указан");
        }
        checkAvailabilityOfUser(user.getId());
        log.trace("Проверка корректности логина: " + user.getLogin());
        checkLogin(user.getLogin());
        log.trace("Проверка на дубликат email: " + user.getEmail());
        checkDuplicatedEmailForUpdate(user);
        return validatedName(user);
    }
}
