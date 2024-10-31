package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.model.Friendships;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FriendshipsRepository;

import ru.yandex.practicum.filmorate.storage.dal.mappers.FriendshipsRowMaapper;
import ru.yandex.practicum.filmorate.storage.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.FilmoRateUserStorageTests.createUserForTest;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class, FriendshipsRepository.class, FriendshipsRowMaapper.class})
public class FilmoRateFriendshipsTests {
    private final UserDbStorage userRepository;
    private final FriendshipsRepository friendshipsRepository;

    @Test
    public void testSendRequestFriend() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friendshipsRepository.request(user.getId(), friend.getId());
        Optional<Friendships> userFriendships = friendshipsRepository.getFriend(user.getId(), friend.getId());
        User finalUser = friend;
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("id", finalUser.getId()));
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("status", Status.UNCONFIRMED));
    }

    @Test
    public void teatUpdateStatusOnConfirmed() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friendshipsRepository.request(user.getId(), friend.getId());
        friendshipsRepository.updateStatusOnConfirmed(user.getId(), friend.getId());
        Optional<Friendships> userFriendships = friendshipsRepository.getFriend(user.getId(), friend.getId());
        User finalUser = friend;
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("id", finalUser.getId()));
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("status", Status.CONFIRMED));
    }

    @Test
    public void testAddFriend() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        Optional<Friendships> userFriendships = friendshipsRepository.getFriend(user.getId(), friend.getId());
        User finalUser = friend;
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("id", finalUser.getId()));
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("status", Status.CONFIRMED));
    }

    @Test
    public void testUpdateStatusOnUnconfirmed() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        friendshipsRepository.updateStatusOnUnconfirmed(user.getId(), friend.getId());
        Optional<Friendships> userFriendships = friendshipsRepository.getFriend(user.getId(), friend.getId());
        User finalUser = friend;
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("id", finalUser.getId()));
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("status", Status.UNCONFIRMED));
    }

    @Test
    public void testDeleteFriend() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friendshipsRepository.request(user.getId(), friend.getId());
        friendshipsRepository.deleteFriend(user.getId(), friend.getId());
        Optional<Friendships> userFriendships = friendshipsRepository.getFriend(user.getId(), friend.getId());
        assertThat(userFriendships).isEmpty();
    }

    @Test
    public void testFindAllFriends() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        User friend1 = new User();
        friend1.setEmail("f1@yandex.ru");
        friend1.setLogin("friend1");
        friend1.setBirthday(LocalDate.of(2001, 9, 5));
        friend1.setName("friend1");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friend1 = userRepository.create(friend1);
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        friendshipsRepository.addFriend(user.getId(), friend1.getId());
        assertEquals(2, userRepository.getAllFriend(user.getId()).size());
    }

    @Test
    public void testFindCommonFriends() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        User friend1 = new User();
        friend1.setEmail("f1@yandex.ru");
        friend1.setLogin("friend1");
        friend1.setBirthday(LocalDate.of(2001, 9, 5));
        friend1.setName("friend1");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friend1 = userRepository.create(friend1);
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        friendshipsRepository.addFriend(friend1.getId(), friend.getId());
        List<User> commonFriends = userRepository.getCommonFriend(user.getId(), friend1.getId());
        assertEquals(1, commonFriends.size());
        assertEquals(friend.getId(), commonFriends.getFirst().getId());
    }
}
