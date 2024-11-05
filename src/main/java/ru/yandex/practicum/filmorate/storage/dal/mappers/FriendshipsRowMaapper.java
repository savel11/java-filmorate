package ru.yandex.practicum.filmorate.storage.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendships;
import ru.yandex.practicum.filmorate.model.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendshipsRowMaapper implements RowMapper<Friendships> {
    @Override
    public Friendships mapRow(ResultSet rs, int rowNum) throws SQLException {
        Friendships friendships = new Friendships();
        friendships.setUserId1(rs.getLong("user_id1"));
        friendships.setId(rs.getLong("user_id2"));
        friendships.setStatus(Status.getStatus(rs.getString("status")));
        return friendships;
    }
}
