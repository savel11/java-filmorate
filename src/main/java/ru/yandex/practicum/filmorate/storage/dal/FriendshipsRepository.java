package ru.yandex.practicum.filmorate.storage.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Friendships;


import java.util.Optional;

@Repository
public class FriendshipsRepository extends BaseRepository<Friendships> {
    private static final String REQUEST_FRIEND_QUERY = "INSERT INTO friendships(user_id1, user_id2, status) " +
            "VALUES(?, ?, 'Unconfirmed')";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO friendships(user_id1, user_id2, status) " +
            "VALUES(?, ?, 'Confirmed')";
    private static final String UPDATE_STATUS_ON_CONFIRMED_QUERY = "UPDATE friendships SET status = 'Confirmed' " +
            "WHERE user_id1 = ? AND user_id2 = ?";
    private static final String UPDATE_STATUS_ON_UNCONFIRMED_QUERY = "UPDATE friendships SET status = 'Unconfirmed' " +
            " WHERE user_id1 = ? AND user_id2 = ?";
    private static final String DELETED_FRIEND_QUERY = "DELETE FROM friendships WHERE user_id1 = ? AND user_id2 = ?";
    private static final String FIND_FRIEND_BY_ID_QUERY = "SELECT * FROM friendships WHERE" +
            " user_id1 = ? AND user_id2 = ?";

    public FriendshipsRepository(JdbcTemplate jdbc, RowMapper<Friendships> mapper) {
        super(jdbc, mapper, Friendships.class);
    }

    public void request(Long userId1, Long userId2) {
        int rowSave = jdbc.update(REQUEST_FRIEND_QUERY, userId1, userId2);
        if (rowSave == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    public void updateStatusOnConfirmed(Long userId1, Long userId2) {
        int rowSave = jdbc.update(UPDATE_STATUS_ON_CONFIRMED_QUERY, userId1, userId2);
        if (rowSave == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    public void addFriend(Long userId1, Long userId2) {
        int rowSave = jdbc.update(ADD_FRIEND_QUERY, userId1, userId2);
        if (rowSave == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    public void updateStatusOnUnconfirmed(Long userId1, Long userId2) {
        update(UPDATE_STATUS_ON_UNCONFIRMED_QUERY, userId1, userId2);
    }

    public void deleteFriend(Long userId1, Long userId2) {
        delete(DELETED_FRIEND_QUERY, userId1, userId2);
    }

    public Optional<Friendships> getFriend(Long userId1, Long userId2) {
        return findOne(FIND_FRIEND_BY_ID_QUERY, userId1, userId2);
    }
}
