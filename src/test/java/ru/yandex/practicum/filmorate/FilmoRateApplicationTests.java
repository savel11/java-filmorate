package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dal.*;
import ru.yandex.practicum.filmorate.storage.dal.mappers.*;

import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingDbStorage;


import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class, FilmRepository.class, FilmRowMapper.class, FilmGenreRepository.class,
        FilmGenreRowMapper.class, FavoriteFilmsRepository.class, FavoritesFilmsRowMapper.class, GenreDbStorage.class,
        GenreRowMapper.class, RatingDbStorage.class, RatingRowMapper.class, FriendshipsRepository.class,
        FriendshipsRowMaapper.class})
public class FilmoRateApplicationTests {
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final FavoriteFilmsRepository favoriteFilmsRepository;
    private final GenreDbStorage genreStorage;
    private final RatingDbStorage ratingDbStorage;
    private final FriendshipsRepository friendshipsRepository;

    @Test
    public void testCreateUser() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        checkEqualsUserFields(user, userFromBd);
    }

    @Test
    public void testFindUserById() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        Optional<User> userOptional = userRepository.findById(userFromBd.getId());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(us ->
                        assertThat(us).hasFieldOrPropertyWithValue("id", userFromBd.getId())
                );
    }

    @Test
    public void testFindUserByEmail() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        Optional<User> userOptional = userRepository.findByEmail(userFromBd.getEmail());
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(us ->
                        assertThat(us).hasFieldOrPropertyWithValue("email", userFromBd.getEmail())
                );
    }

    @Test
    public void testUpdateUser() {
        User user = createUserForTest();
        User userFromBd = userRepository.create(user);
        user.setId(userFromBd.getId());
        user.setEmail("new@yandex.ru");
        user.setLogin("newLogin");
        User updateUser = userRepository.update(user);
        checkEqualsUserFields(user, updateUser);
    }

    @Test
    public void testFindAllUsers() {
        userRepository.create(createUserForTest());
        User user = createUserForTest();
        user.setEmail("new@yandex.ru");
        userRepository.create(user);
        assertEquals(2, userRepository.findAll().size(), "Неверное количество пользователей");
    }

    @Test
    public void testFindDuplicateUserByEmail() {
        User user = userRepository.create(createUserForTest());
        Optional<User> userWithSameEmail = userRepository.findDuplicate(user.getEmail(), 4L);
        assertThat(userWithSameEmail)
                .isPresent()
                .hasValueSatisfying(us ->
                        assertThat(us).hasFieldOrPropertyWithValue("email", user.getEmail())
                );

    }

    @Test
    public void testDeleteUserById() {
        User user = userRepository.create(createUserForTest());
        userRepository.deleteById(user.getId());
        Optional<User> userOptional = userRepository.findById(user.getId());
        assertThat(userOptional).isEmpty();
    }

    @Test
    public void testCreateFilm() {
        Film film = filmRepository.create(createFilmForTest());
        checkEqualsFilmFields(createFilmForTest(), film);
    }

    @Test
    public void testFindFilmById() {
        Film film = filmRepository.create(createFilmForTest());
        Optional<Film> optionalFilm = filmRepository.findFilmById(film.getId());
        assertThat(optionalFilm)
                .isPresent()
                .hasValueSatisfying(fl ->
                        assertThat(fl).hasFieldOrPropertyWithValue("id", film.getId())
                );

    }

    @Test
    public void testUpdateFilm() {
        Film film = filmRepository.create(createFilmForTest());
        Film newFilm = createFilmForTest();
        newFilm.setName("newName");
        newFilm.setDuration(Duration.ofMinutes(100));
        newFilm.setRatingId(2);
        newFilm.setId(film.getId());
        newFilm.setReleaseDate(LocalDate.of(2012, 12, 12));
        Film updateFilm = filmRepository.update(newFilm);
        checkEqualsFilmFields(newFilm, updateFilm);
    }

    @Test
    public void testFindAllFilms() {
        filmRepository.create(createFilmForTest());
        Film film = createFilmForTest();
        film.setName("newFilm");
        filmRepository.create(film);
        assertEquals(2, filmRepository.findAll().size(), "Неверное количество фильмов");
    }

    @Test
    public void testAddLikeFilm() {
        Film film = filmRepository.create(createFilmForTest());
        Optional<Film> filmOptional = filmRepository.addLikeFilm(film.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(fl ->
                        assertThat(fl).hasFieldOrPropertyWithValue("likes", 1L)
                );
    }

    @Test
    public void testDeleteLikeFilm() {
        Film film = filmRepository.create(createFilmForTest());
        filmRepository.addLikeFilm(film.getId());
        Optional<Film> filmOptional = filmRepository.deleteLikeFilm(film.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(fl ->
                        assertThat(fl).hasFieldOrPropertyWithValue("likes", 0L)
                );
    }

    @Test
    public void testFindPopularsFilm() {
        Film film = filmRepository.create(createFilmForTest());
        filmRepository.addLikeFilm(film.getId());
        Film newFilm = createFilmForTest();
        newFilm.setName("newFilm");
        filmRepository.create(newFilm);
        assertEquals(film.getName(), filmRepository.findPopularsFilm(2).getFirst().getName(),
                "Неверный популярный фильм");
        assertEquals(2, filmRepository.findPopularsFilm(2).size(), "Неверное колличесвто фильмов");
    }

    @Test
    public void testDeleteFilm() {
        Film film = filmRepository.create(createFilmForTest());
        filmRepository.deleteFilmById(film.getId());
        Optional<Film> filmOptional = filmRepository.findFilmById(film.getId());
        assertThat(filmOptional).isEmpty();
    }

    @Test
    public void testAddFilmGenre() {
        Film film = filmRepository.create(createFilmForTest());
        filmGenreRepository.addFilmGenre(film.getId(), 1L);
        Optional<FilmGenre> filmGenreOptional = filmGenreRepository.getFilmGenreByID(film.getId(), 1L);
        assertThat(filmGenreOptional).isPresent().hasValueSatisfying(fg -> assertThat(fg)
                .hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    public void testFindAllFilmGenres() {
        Film film = filmRepository.create(createFilmForTest());
        filmGenreRepository.addFilmGenre(film.getId(), 1L);
        filmGenreRepository.addFilmGenre(film.getId(), 2L);
        assertEquals(2, filmGenreRepository.getAllFilmGenres(film.getId()).size(),
                "Неверное количество жанров");
    }

    @Test
    public void testDeleteFilmGenre() {
        Film film = filmRepository.create(createFilmForTest());
        filmGenreRepository.addFilmGenre(film.getId(), 1L);
        filmGenreRepository.addFilmGenre(film.getId(), 2L);
        filmGenreRepository.deleteFilmGenre(film.getId(), 1L);
        assertEquals(1, filmGenreRepository.getAllFilmGenres(film.getId()).size(),
                "Неверное количество жанров");
    }

    @Test
    public void addFavoriteFilm() {
        Film film = filmRepository.create(createFilmForTest());
        User user = userRepository.create(createUserForTest());
        favoriteFilmsRepository.addFavoriteFilm(user.getId(), film.getId());
        Optional<FavoriteFilms> favoriteFilmsOptional = favoriteFilmsRepository.findFavoriteFilm(user.getId(),
                film.getId());
        assertThat(favoriteFilmsOptional).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("filmId", film.getId()));
        assertThat(favoriteFilmsOptional).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("userId", user.getId()));
    }

    @Test
    public void testDeleteFavoriteFilm() {
        Film film = filmRepository.create(createFilmForTest());
        User user = userRepository.create(createUserForTest());
        favoriteFilmsRepository.addFavoriteFilm(user.getId(), film.getId());
        favoriteFilmsRepository.deleteFavoriteFilm(user.getId(), film.getId());
        Optional<FavoriteFilms> favoriteFilmsOptional = favoriteFilmsRepository.findFavoriteFilm(user.getId(),
                film.getId());
        assertThat(favoriteFilmsOptional).isEmpty();
    }

    @Test
    public void testCreateGenre() {
        Genre genre = new Genre();
        genre.setName("Ужасы");
        Genre genreDb = genreStorage.addGenre(genre);
        assertEquals(genre.getName(), genreDb.getName(), "Жанр не коррекстно добавился");
    }

    @Test
    public void testFindGenreByID() {
        Optional<Genre> optionalGenre = genreStorage.getGenreById(1L);
        assertThat(optionalGenre).isPresent().hasValueSatisfying(g -> assertThat(g)
                .hasFieldOrPropertyWithValue("id", 1L));
        assertThat(optionalGenre).isPresent().hasValueSatisfying(g -> assertThat(g)
                .hasFieldOrPropertyWithValue("name", "Комедия"));
    }

    @Test
    public void testFindAllGenres() {
        assertEquals(6, genreStorage.getAll().size(), "Неверное количесвто жанров");
    }

    @Test
    public void testUpdateGenre() {
        Genre genre = new Genre();
        genre.setName("Ужасы");
        Genre genreDb = genreStorage.addGenre(genre);
        genre.setName("New");
        genre.setId(genreDb.getId());
        assertEquals("New", genreStorage.update(genre).getName());
    }

    @Test
    public void testDeleteGenre() {
        Genre genre = new Genre();
        genre.setName("Ужасы");
        Genre genreDb = genreStorage.addGenre(genre);
        genreStorage.deleteGenre(genreDb.getId());
        Optional<Genre> genreOptional = genreStorage.getGenreById(genreDb.getId());
        assertThat(genreOptional).isEmpty();
    }

    @Test
    public void testFindRatingById() {
        Optional<FilmRating> filmRating = ratingDbStorage.getFilmRatingById(1);
        assertThat(filmRating).isPresent().hasValueSatisfying(fr -> assertThat(fr)
                .hasFieldOrPropertyWithValue("id", 1));
        assertThat(filmRating).isPresent().hasValueSatisfying(fr -> assertThat(fr)
                .hasFieldOrPropertyWithValue("name", Rating.G));
    }

    @Test
    public void testFindAllRating() {
        assertEquals(5, ratingDbStorage.getAll().size());
    }

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
                .hasFieldOrPropertyWithValue("userId2", finalUser.getId()));
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("status", Status.Unconfirmed));
    }

    @Test
    public void teatAddFriend() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friendshipsRepository.request(user.getId(), friend.getId());
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        Optional<Friendships> userFriendships = friendshipsRepository.getFriend(user.getId(), friend.getId());
        User finalUser = friend;
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("userId2", finalUser.getId()));
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("status", Status.Confirmed));
    }

    @Test
    public void testUpdateStatus() {
        User user = createUserForTest();
        User friend = new User();
        friend.setEmail("f@yandex.ru");
        friend.setLogin("friend");
        friend.setBirthday(LocalDate.of(2001, 9, 5));
        friend.setName("friend");
        user = userRepository.create(user);
        friend = userRepository.create(friend);
        friendshipsRepository.request(user.getId(), friend.getId());
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        friendshipsRepository.updateStatus(user.getId(), friend.getId());
        Optional<Friendships> userFriendships = friendshipsRepository.getFriend(user.getId(), friend.getId());
        User finalUser = friend;
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("userId2", finalUser.getId()));
        assertThat(userFriendships).isPresent().hasValueSatisfying(ff -> assertThat(ff)
                .hasFieldOrPropertyWithValue("status", Status.Unconfirmed));
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
        friendshipsRepository.request(user.getId(), friend.getId());
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        friendshipsRepository.request(user.getId(), friend1.getId());
        friendshipsRepository.addFriend(user.getId(), friend1.getId());
        assertEquals(2, friendshipsRepository.getAllFriend(user.getId()).size());
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
        friendshipsRepository.request(user.getId(), friend.getId());
        friendshipsRepository.addFriend(user.getId(), friend.getId());
        friendshipsRepository.request(friend1.getId(), friend.getId());
        friendshipsRepository.addFriend(friend1.getId(), friend.getId());
        assertEquals(1, friendshipsRepository.getCommonFriend(user.getId(), friend1.getId()).size());
        assertEquals(friend.getId(), friendshipsRepository.getCommonFriend(user.getId(), friend1.getId()).getFirst()
                .getUserId2());
    }

    private Film createFilmForTest() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Film for test");
        film.setDuration(Duration.ofMinutes(60));
        film.setReleaseDate(LocalDate.of(2000, 2, 2));
        film.setRatingId(1);
        return film;
    }

    private void checkEqualsFilmFields(Film film, Film filmFromBd) {
        assertEquals(film.getName(), filmFromBd.getName(), "Неверное название фильма");
        assertEquals(film.getDescription(), filmFromBd.getDescription(), "Неверное описание фильма");
        assertEquals(film.getDuration(), filmFromBd.getDuration(), "Неверная длительность фильма");
        assertEquals(film.getReleaseDate(), filmFromBd.getReleaseDate(), "Неверная дата релиза фильма");
        assertEquals(film.getRatingId(), filmFromBd.getRatingId(), "Неверный рейтинг фильма");
    }


    private void checkEqualsUserFields(User user, User userFromBd) {
        assertEquals(user.getEmail(), userFromBd.getEmail(), "Неверная почта");
        assertEquals(user.getLogin(), userFromBd.getLogin(), "Неверный логин");
        assertEquals(user.getName(), userFromBd.getName(), "Неверное имя");
        assertEquals(user.getBirthday(), userFromBd.getBirthday(), "Неверная дата рождения");
    }

    private User createUserForTest() {
        User user = new User();
        user.setEmail("s@yandex.ru");
        user.setLogin("login");
        user.setBirthday(LocalDate.of(2001, 9, 5));
        user.setName("name");
        return user;
    }
}
