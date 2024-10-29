package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dal.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("GenreDbStorage")
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final String ADD_GENRE_QUERY = "INSERT INTO genre(name) VALUES(?)";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_ALL_GENRE_QUERY = "SELECT * FROM genre";
    private static final String UPDATE_GENRE_BY_ID_QUERY = "UPDATE genre SET name = ? WHERE genre_id = ?";
    private static final String DELETE_GENRE_QUERY = "DELETE FROM genre WHERE genre_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public Genre addGenre(Genre genre) {
        long id = insert(ADD_GENRE_QUERY, genre.getName());
        genre.setId(id);
        return genre;
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        return findOne(FIND_GENRE_BY_ID_QUERY, id);
    }

    @Override
    public List<Genre> getAll() {
        return findMany(FIND_ALL_GENRE_QUERY);
    }

    @Override
    public Genre update(Genre genre) {
        update(UPDATE_GENRE_BY_ID_QUERY, genre.getName(), genre.getId());
        return genre;
    }

    @Override
    public boolean deleteGenre(Long id) {
        return delete(DELETE_GENRE_QUERY, id);
    }
}
