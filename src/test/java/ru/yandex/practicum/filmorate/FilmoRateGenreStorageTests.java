package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class FilmoRateGenreStorageTests {
    private final GenreDbStorage genreStorage;

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
}
