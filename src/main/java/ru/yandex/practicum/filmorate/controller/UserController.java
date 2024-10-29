package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UsersService;


import java.util.List;


@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UsersService usersService;

    @GetMapping
    public List<UserDto> getAll() {
        return usersService.getAl();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserDto user) {
        return usersService.create(user);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserDto newUser) {
        return usersService.update(newUser);
    }


    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable @Positive Long userId) {
        return usersService.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable @Positive Long userId) {
        usersService.deleteUserById(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public UserDto addFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        return usersService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deletedFriend(@PathVariable @Positive Long id, @PathVariable @Positive Long friendId) {
        usersService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable @Positive Long id) {
        return usersService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable @Positive Long id, @PathVariable @Positive Long otherId) {
        return usersService.getCommonFriends(id, otherId);
    }
}
