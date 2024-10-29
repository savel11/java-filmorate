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
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
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
        checkExistUserByID(updateUserDto.getId());
        User user = userStorage.getUserById(updateUserDto.getId()).get();
        if (updateUserDto.hasEmail()) {
            log.trace("Проверяем свободно ли новая почта");
            if (userStorage.isDuplicateEmailForUpdate(updateUserDto)) {
                log.warn("Пользователь не был обновлен: Пользователь с таким email уже существует");
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            log.trace("Почта свободно");
            user.setEmail(updateUserDto.getEmail());
        }
        if (updateUserDto.hasLogin()) {
            checkLogin(updateUserDto.getLogin());
            user.setLogin(updateUserDto.getLogin());
        }
        if (updateUserDto.hasName()) {
            user.setName(updateUserDto.getName());
        }
        if (updateUserDto.hasBirthday()) {
            user.setBirthday(updateUserDto.getBirthday());
        }
        log.trace("Данные пользователя успешно обновленны");
        user = userStorage.update(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto getUserById(Long userId) {
        checkExistUserByID(userId);
        return UserMapper.mapToUserDto(userStorage.getUserById(userId).get());
    }

    public void deleteUserById(Long userId) {
        if (!userStorage.deleteUserById(userId)) {
            log.warn("Пользователь не был удален: пользователь с указанным id не существует");
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    public UserDto addFriend(Long userId1, Long userId2) {
        checkExistUserByID(userId1);
        checkExistUserByID(userId2);
        log.trace("Проверяем есть ли пользоваетель c id " + userId1 + " в друзьях у пользоваетля с id " + userId2);
        if (userStorage.getFriend(userId2, userId1).isPresent()) {
            log.trace("Проверяем статус заявки");
            if (userStorage.getFriend(userId2, userId1).get().getStatus().equals(Status.Confirmed)) {
                log.warn("Заявка уже была отправлена");
                throw new DuplicatedDataException("Пользователь уже получил заявку в друзья");
            }
            if (userStorage.getFriend(userId2, userId1).get().getStatus().equals(Status.Unconfirmed)) {
                log.trace("Пользователь оставлял заявку на дружбу с вами");
                log.trace("Подтверждаем дружбу");
                userStorage.addFriend(userStorage.getUserById(userId1).get(), userStorage.getUserById(userId2).get());
                log.trace("Дружба подтверждена");
                return UserMapper.mapToUserDto(userStorage.getUserById(userId2).get());
            }
        }
        log.trace("Отправляем запрос на дружбу");
        userStorage.request(userStorage.getUserById(userId2).get(), userStorage.getUserById(userId1).get());
        log.trace("Запрос отправлен");
        return UserMapper.mapToUserDto(userStorage.getUserById(userId2).get());
    }

    public void deleteFriend(Long userId1, Long userId2) {
        checkExistUserByID(userId1);
        checkExistUserByID(userId2);
        log.trace("Проверяем статус дружбы");
        if (userStorage.getFriend(userId2, userId1).isEmpty()) {
            log.trace("Пользователь не является другом, удалять ничего не надо");
            return;
        }
        if (userStorage.getFriend(userId2, userId1).get().getStatus().equals(Status.Confirmed)) {
            log.trace("Пользователь в друзьях у пользователя");
            userStorage.updateStatus(userStorage.getUserById(userId2).get(), userStorage.getUserById(userId1).get());
            log.trace("Переместили пользователя в подписчики");
            return;
        }
        if (userStorage.getFriend(userId2, userId1).get().getStatus().equals(Status.Unconfirmed)) {
            log.trace("Пользователь подписан на пользователя");
            userStorage.updateStatus(userStorage.getUserById(userId1).get(), userStorage.getUserById(userId2).get());
            userStorage.deleteFriend(userStorage.getUserById(userId2).get(), userStorage.getUserById(userId1).get());
            userStorage.deleteFriend(userStorage.getUserById(userId1).get(), userStorage.getUserById(userId2).get());
            log.trace("Отозвали заявку");
        }
    }

    public List<UserDto> getCommonFriends(Long userId1, Long userId2) {
        checkExistUserByID(userId1);
        checkExistUserByID(userId2);
        return userStorage.getCommonFriend(userId1, userId2).stream().map(el -> UserMapper.mapToUserDto(
                userStorage.getUserById(el.getUserId2()).get())).collect(Collectors.toList());
    }

    public List<UserDto> getFriends(Long id) {
        checkExistUserByID(id);
        return userStorage.getAllFriend(id).stream().map(el -> UserMapper.mapToUserDto(
                userStorage.getUserById(el.getUserId2()).get())).collect(Collectors.toList());
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

    private void checkExistUserByID(Long id) {
        log.trace("Проверка существование пользователья с id = " + id);
        if (userStorage.getUserById(id).isEmpty()) {
            log.warn("Пользователя с id = " + id + " не существует");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        log.trace("Пользователь существует");
    }
}
