package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.user.NewUserDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(NewUserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setLogin(userDto.getLogin());
        user.setEmail(userDto.getEmail());
        user.setBirthday(userDto.getBirthday());
        return user;
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setLogin(user.getLogin());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setBirthday(user.getBirthday());
        return userDto;
    }
}
