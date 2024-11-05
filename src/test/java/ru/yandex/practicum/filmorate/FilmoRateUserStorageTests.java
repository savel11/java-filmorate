package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendshipsRepository;

import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendshipsRowMaapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;


import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FriendshipsRepository.class, FriendshipsRowMaapper.class})
public class FilmoRateUserStorageTests {
    private final UserDbStorage userRepository;

    @Test
    public void testCreateUser() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        checkEqualsUserFields(user, userFromBd);
    }

    @Test
    public void testFindUserById() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        Optional<User> userOptional = userRepository.getUserById(userFromBd.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(us ->
                        assertThat(us).hasFieldOrPropertyWithValue("id", userFromBd.getId())
                );
    }

    @Test
    public void testFindUserByEmail() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        Optional<User> userOptional = userRepository.getUserByEmail(userFromBd.getEmail());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(us ->
                        assertThat(us).hasFieldOrPropertyWithValue("email", userFromBd.getEmail())
                );
    }

    @Test
    public void testUpdateUser() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        user.setId(userFromBd.getId());
        user.setEmail("new@yandex.ru");
        user.setLogin("newLogin");
        User updateUser = userRepository.update(user);
        checkEqualsUserFields(user, updateUser);
    }

    @Test
    public void testFindAllUsers() {
        userRepository.create(createUserForTest());
        User user = createUserForTest();
        user.setEmail("new@yandex.ru");
        userRepository.create(user);
        assertEquals(2, userRepository.getAll().size(), "Неверное количество пользователей");
    }

    @Test
    public void testFindDuplicateUserByEmail() {
        User user = userRepository.create(createUserForTest());
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setEmail(user.getEmail());
        updateUserDto.setName("NewName");
        updateUserDto.setId(100L);
        boolean isDuplicate = userRepository.isDuplicateEmailForUpdate(updateUserDto);
        assertTrue(isDuplicate, "Дубликат не был обнаружен");
    }

    @Test
    public void testDeleteUserById() {
        User user = userRepository.create(createUserForTest());
        userRepository.deleteUserById(user.getId());
        Optional<User> userOptional = userRepository.getUserById(user.getId());
        assertThat(userOptional).isEmpty();
    }


    private void checkEqualsUserFields(User user, User userFromBd) {
        assertEquals(user.getEmail(), userFromBd.getEmail(), "Неверная почта");
        assertEquals(user.getLogin(), userFromBd.getLogin(), "Неверный логин");
        assertEquals(user.getName(), userFromBd.getName(), "Неверное имя");
        assertEquals(user.getBirthday(), userFromBd.getBirthday(), "Неверная дата рождения");
    }

    public static User createUserForTest() {
        User user = new User();
        user.setEmail("s@yandex.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2001, 9, 5));
        user.setName("name");
        return user;
    }
}

