package ru.yandex.practicum.filmorate.storage.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendships;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    private Long generatedId() {
        return ++id;
    }

    @Override
    public List<User> getAll() {
        return users.values().stream().toList();
    }

    @Override
    public User create(User user) {
        user.setId(generatedId());
        users.put(user.getId(), new User(user.getId(), user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), new HashSet<>(), new HashSet<>()));
        return user;
    }

    @Override
    public User update(User user) {
        User oldUser = users.get(user.getId());
        user.setFriends(oldUser.getFriends());
        user.setLikedFilms(oldUser.getLikedFilms());
        users.put(user.getId(), new User(user.getId(), user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), user.getLikedFilms(), user.getFriends()));
        return user;
    }


    @Override
    public Optional<User> getUserById(Long id) {
        checkAvailabilityOfUser(id);
        User user = users.get(id);
        return Optional.of(new User(user.getId(), user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getLikedFilms(), user.getFriends()));
    }

    @Override
    public boolean deleteUserById(Long id) {
        checkAvailabilityOfUser(id);
        return users.remove(id) != null;
    }

    @Override
    public void updateStatusOnConfirmed(User user1, User user2) {
        user2.getFriends().remove(new Friendships(user2.getId(), user1.getId(), Status.UNCONFIRMED));
        user2.getFriends().add(new Friendships(user2.getId(), user1.getId(), Status.CONFIRMED));
        updateFriends(user2);
    }


    @Override
    public void request(User user1, User user2) {
        Friendships friendships = new Friendships(user1.getId(), user2.getId(), Status.UNCONFIRMED);
        user1.getFriends().add(friendships);
        user2.getFriends().add(new Friendships(user2.getId(), user1.getId(), Status.CONFIRMED));
        updateFriends(user1);
        updateFriends(user2);
    }

    @Override
    public void updateStatusOnUnconfirmed(User user1, User user2) {
        user2.getFriends().remove(new Friendships(user2.getId(), user1.getId(), Status.CONFIRMED));
        user2.getFriends().add(new Friendships(user2.getId(), user1.getId(), Status.UNCONFIRMED));
        updateFriends(user2);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteFriend(User user1, User user2) {
        user2.getFriends().remove(new Friendships(user2.getId(), user1.getId(), Status.UNCONFIRMED));
        updateFriends(user2);
    }

    @Override
    public List<User> getAllFriend(Long userId1) {
        return users.get(userId1).getFriends().stream().filter(f -> f.getStatus().equals(Status.CONFIRMED))
                .map(f -> getUserById(f.getId()).get()).toList();
    }

    @Override
    public Optional<Friendships> getFriend(Long userId1, Long userId2) {
        return users.get(userId1).getFriends().stream().filter(f -> f.getId().equals(userId2) &&
                f.getStatus().equals(Status.CONFIRMED)).findFirst();
    }

    @Override
    public List<User> getCommonFriend(Long userId1, Long userId2) {
        Set<Friendships> friendsUser1 = users.get(userId1).getFriends().stream().filter(f -> f.getStatus().equals(
                Status.CONFIRMED)).collect(Collectors.toSet());
        Set<Friendships> friendsUser2 = users.get(userId2).getFriends().stream().filter(f -> f.getStatus().equals(
                Status.CONFIRMED)).collect(Collectors.toSet());
        if (friendsUser1.isEmpty() || friendsUser2.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Friendships> commonFriends = new HashSet<>(friendsUser1);
        if (commonFriends.retainAll(friendsUser2)) {
            return commonFriends.stream().map(f -> getUserById(f.getId()).get()).toList();
        }
        return Collections.emptyList();
    }


    public void updateFriends(User user) {
        users.put(user.getId(), user);
    }

    public void updateLikesFilms(User user) {
        users.put(user.getId(), user);
    }

    private void checkAvailabilityOfUser(Long id) {
        if (!users.containsKey(id)) {
            log.warn("Данные не обновлены: Пользователя с id = " + id + " не существует");
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    @Override
    public boolean isDuplicateEmailForUpdate(UpdateUserDto updateUserDto) {
        return users.values().stream().filter(u -> !u.equals(users.get(updateUserDto.getId())))
                .map(User::getEmail).anyMatch(e -> e.equals(updateUserDto.getEmail()));
    }
}
