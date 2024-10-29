package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.Friendships;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FavoriteFilmsRepository;
import ru.yandex.practicum.filmorate.storage.dal.FriendshipsRepository;
import ru.yandex.practicum.filmorate.storage.dal.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final UserRepository userRepository;
    private final FavoriteFilmsRepository favoriteFilmsRepository;
    private final FriendshipsRepository friendshipsRepository;

    @Override
    public Collection<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User create(User user) {
        return userRepository.create(user);
    }

    @Override
    public User update(User newUser) {
        return userRepository.update(newUser);
    }


    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public boolean deleteUserById(Long id) { // сделать boolean везде, в сервисе проверять удалился или нет
        return userRepository.deleteById(id);
    }

    @Override
    public void addFriend(User user1, User user2) {
        friendshipsRepository.addFriend(user2.getId(), user1.getId());
    }

    @Override
    public void request(User user1, User user2) {
        friendshipsRepository.request(user1.getId(), user2.getId());
        friendshipsRepository.request(user2.getId(), user1.getId());
        friendshipsRepository.addFriend(user2.getId(), user1.getId());

    }

    @Override
    public void updateStatus(User user1, User user2) {
        friendshipsRepository.updateStatus(user2.getId(), user1.getId());
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void deleteFriend(User user1, User user2) {
        friendshipsRepository.deleteFriend(user2.getId(), user1.getId());
    }

    @Override
    public List<Friendships> getAllFriend(Long userId1) {
        return friendshipsRepository.getAllFriend(userId1);
    }

    @Override
    public Optional<Friendships> getFriend(Long userId1, Long userId2) {
        return friendshipsRepository.getFriend(userId1, userId2);
    }

    @Override
    public List<Friendships> getCommonFriend(Long userId1, Long userId2) {
        return friendshipsRepository.getCommonFriend(userId1, userId2);
    }

    @Override
    public boolean isDuplicateEmailForUpdate(UpdateUserDto updateUserDto) {
        return userRepository.findDuplicate(updateUserDto.getEmail(), updateUserDto.getId()).isPresent();
    }
}
