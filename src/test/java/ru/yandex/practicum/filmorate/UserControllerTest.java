package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest()
public class UserControllerTest {
    User user;

    @Autowired
    private UserController userController;

    @BeforeEach
    public void createUserForTest() {
        user = new User();
        user.setEmail("user@yandex.ru");
        user.setLogin("UserName");
        user.setName("Name");
        user.setBirthday(LocalDate.of(2001, 5, 9));
    }

    @BeforeEach
    public void clearUsers() {
        userController.deleteAll();
    }

    @Test
    void createUserWithNotCorrectEmail() {
        user.setEmail("user");
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с некорректным email должно приводить к ошибки валидации");
        user.setEmail("user@mail");
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с некорректным email должно приводить к ошибки валидации");
        user.setEmail("user@mail?ru");
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с некорректным email должно приводить к ошибки валидации");
        user.setEmail("us er@mail.ru");
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с некорректным email должно приводить к ошибки валидации");
    }

    @Test
    void createUserWithBlankEmail() {
        user.setEmail("");
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с пустым email должно приводить к ошибки валидации");
        user.setEmail(null);
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с null email должно приводить к ошибки валидации");
    }

    @Test
    void createUserWithBlankUserName() {
        user.setLogin("");
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с пустым логином должно приводить к ошибки валидации");
        user.setLogin(null);
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с null логином должно приводить к ошибки валидации");
    }

    @Test
    void createUserWithSpaceInUserName() {
        user.setLogin("Us er");
        assertThrows(InvalidFormatException.class, () -> userController.create(user),
                "Добавление пользователя с пробелом в логине должно приводить к ошибки валидации");
        user.setLogin("User ");
        assertThrows(InvalidFormatException.class, () -> userController.create(user),
                "Добавление пользователя с пробелом в логине должно приводить к ошибки валидации");
        user.setLogin(" User");
        assertThrows(InvalidFormatException.class, () -> userController.create(user),
                "Добавление пользователя с пробелом в логине должно приводить к ошибки валидации");
    }

    @Test
    void createUserWithNotCorrectBirthday() {
        user.setBirthday(LocalDate.of(2026, 3, 4));
        assertThrows(ConstraintViolationException.class, () -> userController.create(user),
                "Добавление пользователя с датой рождения в будущем должно приводить к ошибки валидации");
    }

    @Test
    void createUserWithDuplicatedEmail() {
        userController.create(user);
        User newUser = new User();
        newUser.setEmail("user@yandex.ru");
        newUser.setLogin("newUserName");
        newUser.setName("newName");
        newUser.setBirthday(LocalDate.of(2005, 5, 9));
        assertThrows(DuplicatedDataException.class, () -> userController.create(newUser),
                "Добавление пользователя с email который уже используется  должно приводить к ошибки валидации");
    }

    @Test
    void createUserWithCorrectData() {
        userController.create(user);
    }

    @Test
    void createUserWithBlankName() {
        user.setName("");
        assertEquals(user.getLogin(), userController.create(user).getName(),
                "При создание пользователя с пустым полем имя логин должен использоваться в качестве имени");
    }

    @Test
    void createUserWithoutName() {
        user.setName(null);
        assertEquals(user.getLogin(), userController.create(user).getName(),
                "При создание пользователя без имени логин должен использоваться в качестве имени");
    }

    @Test
    void getUsers() {
        userController.create(user);
        assertEquals(1, userController.getAll().size(), "Неверное количество пользователей");
    }

    @Test
    void updateUserWithNotCorrectEmail() {
        userController.create(user);
        user.setEmail("user@mail");
        user.setId(1L);
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление email пользователя на некорректным email должно приводить к ошибки валидации");
        user.setEmail("user@mail?ru");
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление email пользователя на некорректным email должно приводить к ошибки валидации");
        user.setEmail("us er@mail.ru");
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление email пользователя на некорректным email должно приводить к ошибки валидации");
    }

    @Test
    void updateUserWithBlankEmail() {
        userController.create(user);
        user.setEmail("");
        user.setId(1L);
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление пользователя с пустым email должно приводить к ошибки валидации");
        user.setEmail(null);
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление пользователя с null email должно приводить к ошибки валидации");
    }

    @Test
    void updateUserWithBlankUserName() {
        userController.create(user);
        user.setLogin("");
        user.setId(1L);
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление пользователя с пустым логином должно приводить к ошибки валидации");
        user.setLogin(null);
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление пользователя с null логином должно приводить к ошибки валидации");
    }

    @Test
    void updateUserWithSpaceInUserName() {
        userController.create(user);
        user.setLogin("Us er");
        user.setId(1L);
        assertThrows(InvalidFormatException.class, () -> userController.update(user),
                "Обновление пользователя с пробелом в логине должно приводить к ошибки валидации");
        user.setLogin("User ");
        assertThrows(InvalidFormatException.class, () -> userController.update(user),
                "Обновление пользователя с пробелом в логине должно приводить к ошибки валидации");
        user.setLogin(" User");
        assertThrows(InvalidFormatException.class, () -> userController.update(user),
                "Обновление пользователя с пробелом в логине должно приводить к ошибки валидации");
    }

    @Test
    void updateUserWithNotCorrectBirthday() {
        userController.create(user);
        user.setBirthday(LocalDate.of(2026, 3, 4));
        user.setId(1L);
        assertThrows(ConstraintViolationException.class, () -> userController.update(user),
                "Обновление пользователя с датой рождения в будущем должно приводить к ошибки валидации");
    }

    @Test
    void updateUserWithDuplicatedEmail() {
        userController.create(user);
        User newUser = new User();
        newUser.setEmail("user1@yandex.ru");
        newUser.setLogin("newUserName");
        newUser.setName("newName");
        newUser.setBirthday(LocalDate.of(2005, 5, 9));
        userController.create(newUser);
        newUser.setId(2L);
        newUser.setEmail("user@yandex.ru");
        assertThrows(DuplicatedDataException.class, () -> userController.update(newUser),
                "Обновление пользователя с email который уже используется  должно приводить к ошибки валидации");
    }

    @Test
    void updateUserWithCorrectData() {
        userController.create(user);
        user.setName("newName");
        user.setId(1L);
        assertEquals(user.getName(), userController.update(user).getName(), "Данные не обновились");
    }

    @Test
    void updateUserWithBlankName() {
        userController.create(user);
        user.setName("");
        user.setId(1L);
        assertEquals(user.getLogin(), userController.update(user).getName(),
                "При обновление пользователя с пустым полем имя логин должен использоваться в качестве имени");
    }
}
