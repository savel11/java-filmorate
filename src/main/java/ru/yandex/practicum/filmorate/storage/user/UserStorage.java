package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.Friendships;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAll();

    User create(User user);

    User update(User newUser);

    Optional<User> getUserById(Long id);

    boolean deleteUserById(Long id);

    void updateStatusOnConfirmed(User user1, User user2);

    void request(User user1, User user2);

    void updateStatusOnUnconfirmed(User user1, User user2);

    Optional<User> getUserByEmail(String email);

    void deleteFriend(User user1, User user2);

    List<User> getAllFriend(Long userId1);

    Optional<Friendships> getFriend(Long userId1, Long userId2);

    List<User> getCommonFriend(Long userId1, Long userId2);

    boolean isDuplicateEmailForUpdate(UpdateUserDto updateUserDto);
}
