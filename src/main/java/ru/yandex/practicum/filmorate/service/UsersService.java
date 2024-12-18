package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Friendships;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UsersService {
    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    public List<UserDto> getAl() {
        return userStorage.getAll().stream().map(UserMapper::mapToUserDto).collect(Collectors.toList());
    }

    public UserDto create(NewUserDto newUserDto) {
        log.trace("Валидация для создания нового пользователя: " + newUserDto);
        checkLogin(newUserDto.getLogin());
        log.trace("Проверка на дубликат email: " + newUserDto.getEmail());
        if (userStorage.getUserByEmail(newUserDto.getEmail()).isPresent()) {
            log.warn("Пользователь не был создан: Пользователь с таким email уже существует");
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        newUserDto.setName(validatedName(newUserDto));
        User user = UserMapper.mapToUser(newUserDto);
        user = userStorage.create(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserDto updateUserDto) {
        log.trace("Валидация для обновления  пользователя:" + updateUserDto);
        if (updateUserDto.getId() == null) {
            log.warn("Данные не обновлены: Для обновление нужно указать id пользователя");
            throw new InvalidFormatException("Id должен быть указан");
        }
        User user = validationUpdateUser(updateUserDto);
        log.trace("Данные пользователя успешно обновленны");
        user = userStorage.update(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto getUserById(Long userId) {
        return UserMapper.mapToUserDto(checkExistUserByID(userId));
    }

    public void deleteUserById(Long userId) {
        if (!userStorage.deleteUserById(userId)) {
            log.warn("Пользователь не был удален: пользователь с указанным id не существует");
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    public UserDto addFriend(Long userId1, Long userId2) {
        User user1 = checkExistUserByID(userId1);
        User user2 = checkExistUserByID(userId2);
        log.trace("Проверяем есть ли пользоваетель c id " + userId1 + " в друзьях у пользоваетля с id " + userId2);
        Optional<Friendships> friendships = userStorage.getFriend(userId2, userId1);
        if (friendships.isPresent()) {
            log.trace("Проверяем статус заявки");
            if (friendships.get().getStatus().equals(Status.CONFIRMED)) {
                log.warn("Заявка уже была отправлена");
                throw new DuplicatedDataException("Пользователь уже получил заявку в друзья");
            }
            if (friendships.get().getStatus().equals(Status.UNCONFIRMED)) {
                log.trace("Пользователь оставлял заявку на дружбу с вами");
                log.trace("Подтверждаем дружбу");
                userStorage.updateStatusOnConfirmed(user1, user2);
                log.trace("Дружба подтверждена");
                return UserMapper.mapToUserDto(user2);
            }
        }
        log.trace("Отправляем запрос на дружбу");
        userStorage.request(user2, user1);
        log.trace("Запрос отправлен");
        return UserMapper.mapToUserDto(user2);
    }

    public void deleteFriend(Long userId1, Long userId2) {
        User user1 = checkExistUserByID(userId1);
        User user2 = checkExistUserByID(userId2);
        log.trace("Проверяем статус дружбы");
        Optional<Friendships> friendships = userStorage.getFriend(userId2, userId1);
        if (friendships.isEmpty()) {
            log.trace("Пользователь не является другом, удалять ничего не надо");
            return;
        }
        if (friendships.get().getStatus().equals(Status.CONFIRMED)) {
            log.trace("Пользователь в друзьях у пользователя");
            userStorage.updateStatusOnUnconfirmed(user2, user1);
            log.trace("Переместили пользователя в подписчики");
            return;
        }
        if (friendships.get().getStatus().equals(Status.UNCONFIRMED)) {
            log.trace("Пользователь подписан на пользователя");
            userStorage.deleteFriend(user2, user1);
            userStorage.deleteFriend(user1, user2);
            log.trace("Отозвали заявку");
        }
    }

    public List<UserDto> getCommonFriends(Long userId1, Long userId2) {
        checkExistUserByID(userId1);
        checkExistUserByID(userId2);
        return userStorage.getCommonFriend(userId1, userId2).stream().map(UserMapper::mapToUserDto).toList();
    }

    public List<UserDto> getFriends(Long id) {
        checkExistUserByID(id);
        return userStorage.getAllFriend(id).stream().map(UserMapper::mapToUserDto).toList();
    }


    private String validatedName(NewUserDto user) {
        log.trace("Проверка имени пользователя: " + user.getName());
        if (user.getName() == null || user.getName().isBlank()) {
            log.trace("Имя пользователя изменяем на его логин: " + user.getLogin());
            return user.getLogin();
        }
        return user.getName();
    }

    private void checkLogin(String login) {
        log.trace("Проверка корректности логина: " + login);
        if (login.contains(" ")) {
            log.warn("Пользователь не был создан: Логин пользователя не должен содержать пробелов");
            throw new InvalidFormatException("Некорректный формат логина: Логин не должен содержать пробелов.");
        }
        log.trace("Логин прошел проверку");
    }

    private User  checkExistUserByID(Long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) {
            log.warn("Пользователя с id = " + id + " не существует");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user.get();
    }

    private User validationUpdateUser(UpdateUserDto updateUserDto) {
        User newUser = checkExistUserByID(updateUserDto.getId());
        if (updateUserDto.hasEmail()) {
            log.trace("Проверяем свободно ли новая почта");
            if (userStorage.isDuplicateEmailForUpdate(updateUserDto)) {
                log.warn("Пользователь не был обновлен: Пользователь с таким email уже существует");
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            log.trace("Почта свободно");
            newUser.setEmail(updateUserDto.getEmail());
        }
        if (updateUserDto.hasLogin()) {
            checkLogin(updateUserDto.getLogin());
            newUser.setLogin(updateUserDto.getLogin());
        }
        if (updateUserDto.hasName()) {
            newUser.setName(updateUserDto.getName());
        }
        if (updateUserDto.hasBirthday()) {
            newUser.setBirthday(updateUserDto.getBirthday());
        }
        return newUser;
    }
}
