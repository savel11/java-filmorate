package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
     Collection<User> getAll();

     User create(User user);

     User update(User newUser);

     void deleteAll();

     User getUserById(Long id);

     void deleteUserById(Long id);
}
