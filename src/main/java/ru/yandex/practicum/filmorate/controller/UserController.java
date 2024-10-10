package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UsersService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;


import java.util.Collection;


@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UsersService usersService;

    @GetMapping
    public Collection<User> getAll() {
        return inMemoryUserStorage.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        return inMemoryUserStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        return inMemoryUserStorage.update(newUser);
    }

    @DeleteMapping
    public void deleteAll() {
        inMemoryUserStorage.deleteAll();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable @Positive Long userId) {
        return inMemoryUserStorage.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable @Positive Long userId) {
        inMemoryUserStorage.deleteUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        return usersService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deletedFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        usersService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable @Positive Long id) {
        return usersService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getCommonFriends(@PathVariable @Positive Long id, @PathVariable @Positive Long otherId) {
        return usersService.getCommonFriends(id, otherId);
    }
}
