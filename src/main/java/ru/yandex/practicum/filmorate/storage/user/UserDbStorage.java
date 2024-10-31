package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserDto;
import ru.yandex.practicum.filmorate.model.Friendships;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.BaseRepository;
import ru.yandex.practicum.filmorate.storage.dal.FriendshipsRepository;


import java.util.List;
import java.util.Optional;


@Repository
@Qualifier("UserDbStorage")
public class UserDbStorage extends BaseRepository<User> implements UserStorage {
    private final FriendshipsRepository friendshipsRepository;
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?) ";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE " +
            "user_id = ?";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String FIND_DUPLICATE_EMAIL_QUERY = "SELECT * FROM users WHERE email = ? " +
            "AND user_id <> ?";
    private static final String FIND_FRIENDS = "SELECT us.* FROM users AS us LEFT OUTER JOIN friendships AS fr ON " +
            "us.user_id = fr.user_id2 WHERE fr.user_id1 = ? AND status = 'Confirmed'";
    private static final String FIND_COMMON_FRIENDS = "SELECT us.* FROM users AS us LEFT OUTER JOIN friendships AS fr ON " +
            "us.user_id = fr.user_id2 WHERE user_id1 = ? AND status = 'Confirmed' AND user_id2 IN (SELECT user_id2 " +
            " FROM friendships WHERE user_id1 = ? AND status = 'Confirmed')";



    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, FriendshipsRepository friendshipsRepository) {
        super(jdbc, mapper, User.class);
        this.friendshipsRepository = friendshipsRepository;
    }

    @Override
    public List<User> getAll() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return findOne(FIND_BY_EMAIL_QUERY, email);
    }

    @Override
    public User create(User user) {
        Long id = insert(INSERT_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public boolean deleteUserById(Long id) {
        return delete(DELETE_QUERY, id);
    }

    @Override
    public boolean isDuplicateEmailForUpdate(UpdateUserDto updateUserDto) {
        return findOne(FIND_DUPLICATE_EMAIL_QUERY, updateUserDto.getEmail(), updateUserDto.getId()).isPresent();
    }

    @Override
    public void updateStatusOnConfirmed(User user1, User user2) {
        friendshipsRepository.updateStatusOnConfirmed(user2.getId(), user1.getId());
    }


    @Override
    public void request(User user1, User user2) {
        friendshipsRepository.request(user1.getId(), user2.getId());
        friendshipsRepository.addFriend(user2.getId(), user1.getId());
    }

    @Override
    public void updateStatusOnUnconfirmed(User user1, User user2) {
        friendshipsRepository.updateStatusOnUnconfirmed(user2.getId(), user1.getId());
    }

    @Override
    public void deleteFriend(User user1, User user2) {
        friendshipsRepository.deleteFriend(user2.getId(), user1.getId());
    }

   @Override
    public List<User> getAllFriend(Long userId1) {
      return findMany(FIND_FRIENDS, userId1);
     }

    @Override
    public Optional<Friendships> getFriend(Long userId1, Long userId2) {
        return friendshipsRepository.getFriend(userId1, userId2);
    }

     @Override
    public List<User> getCommonFriend(Long userId1, Long userId2) {
        return findMany(FIND_COMMON_FRIENDS, userId1, userId2);
    }
}
