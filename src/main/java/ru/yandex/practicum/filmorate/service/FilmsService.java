package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.InvalidFormatException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmsService {
    @Autowired
    @Qualifier("FilmDbStorage")
    private FilmStorage filmStorage;
    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;
    @Autowired
    @Qualifier("GenreDbStorage")
    private GenreStorage genreStorage;
    @Autowired
    @Qualifier("RatingDbStorage")
    private RatingStorage ratingStorage;


    private static final LocalDate MIN_DATA = LocalDate.of(1895, 12, 28);

    public List<FilmDto> getAll() {
        List<FilmDto> films = filmStorage.getAll().stream().map(FilmMapper::mapToFilmDto).toList();
        films.forEach(f -> f.setMpa(ratingStorage.getFilmRatingById(f.getMpa().getId()).get()));
        films.forEach(f -> {
            if (f.getGenres() != null && !f.getGenres().isEmpty()) {
                f.getGenres().forEach(g -> g.setName(genreStorage.getGenreById(g.getId()).get().getName()));
            }
        });
        return films;
    }

    public FilmDto createFilm(NewFilmDto newFilmDto) {
        log.info("Добавляем новый фильм");
        log.debug("Фильм: " + newFilmDto);
        log.trace("Проверка даты релиза:" + newFilmDto.getReleaseDate());
        if (isBeforeMinDate(newFilmDto.getReleaseDate())) {
            log.warn("Фильм не был добавлен: Дата релиза фильма должна быть не раньше 28 декабря 1895 года");
            throw new InvalidFormatException("Некорректный формат даты релиза: Дата релиза должна" +
                    " быть не раньше 28 декабря 1895 года");
        }
        log.trace("Дата релиза корректная");
        log.trace("Проверка длительности фильма: " + newFilmDto.getDuration());
        if (isNegativeDuration(newFilmDto.getDuration())) {
            log.warn("Фильм не был добавлен: Продолжительность не должна быть отрицательным числом");
            throw new InvalidFormatException("Некорректный формат продолжительности фильма: Продолжительность фильма" +
                    " должна быть положителным числом");
        }
        if (newFilmDto.getGenres() != null && !newFilmDto.getGenres().isEmpty()) {
            log.trace("Проверка существования жанров фильма");
            if (isGenreNonExist(newFilmDto.getGenres())) {
                log.warn("Жанр с указанным id не найден");
                throw new InvalidFormatException("Один из жанров не найден");
            }
            newFilmDto.setGenres(newFilmDto.getGenres().stream().distinct().collect(Collectors.toList()));
        }
        log.trace("Жанры существуют");
        log.trace("Проверка существования рейтинга фильма");
        if (isRatingNonExist(newFilmDto.getMpa())) {
            log.warn("Рейтинг с id = " + newFilmDto.getMpa() + " не найден");
            throw new InvalidFormatException("Рейтинг с id = " + newFilmDto.getMpa() + " не существует");
        }
        log.trace("Валидация прошла успешно");
        Film film = FilmMapper.mapToFilm(newFilmDto);
        film = filmStorage.create(film);
        if (newFilmDto.getGenres() != null) {
            filmStorage.addFilmGenre(film.getId(), newFilmDto.getGenres().stream().map(FilmGenre::getId).toList());
            log.info("Фильм успешно добавлен!");
        }
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto updateFilm(UpdateFilmDto updateFilmDto) {
        log.info("Обновляем данные о фильме");
        log.trace("Проверка индефикатора на null");
        if (updateFilmDto.getId() == null) {
            log.warn("Данные не обновленны: Для обновленние нужно указать id фильма");
            throw new InvalidFormatException("Id должен быть указан");
        }
        log.trace("Проверка индефикатора прошла успешно");
        log.trace("Проверка на наличие фильма c индефикатором " + updateFilmDto.getId());
        if (filmStorage.getFilmById(updateFilmDto.getId()).isEmpty()) {
            log.warn("Фильм с указанным id не найден");
            throw new NotFoundException("Фильм с id = " + updateFilmDto.getId() + " не найден");
        }
        log.trace("Проверка на наличие фильма прошла успешно");
        log.trace("Проверка на получение дубликата из-за обновление данных");
        if (filmStorage.isDuplicateForUpdated(updateFilmDto)) {
            log.warn("Данные не обновлены: Такой фильм уже есть");
            throw new DuplicatedDataException("Фильм уже был добавлен");
        }
        Film film = filmStorage.getFilmById(updateFilmDto.getId()).get();
        film.setFilmGenres(filmStorage.getFilmGenresByFilmId(film.getId()));
        log.trace("Оновление данных не приводит к дубликату");
        if (updateFilmDto.hasReleaseDate()) {
            log.trace("Проверка даты релиза:" + updateFilmDto.getReleaseDate());
            if (isBeforeMinDate(updateFilmDto.getReleaseDate())) {
                log.warn("Данные не обновленны: Дата релиза фильма должна быть не раньше 28 декабря 1895 года");
                throw new InvalidFormatException("Некорректный формат даты релиза: Дата релиза должна" +
                        " быть не раньше 28 декабря 1895 года");
            }
            film.setReleaseDate(updateFilmDto.getReleaseDate());
            log.trace("Дата релиза корректная");
        }
        if (updateFilmDto.hasDuration()) {
            log.trace("Проверка длительности фильма: " + updateFilmDto.getDuration());
            if (isNegativeDuration(updateFilmDto.getDuration())) {
                log.warn("Данные не были обновленны: Продолжительность не должна быть отрицательным числом");
                throw new InvalidFormatException("Некорректный формат продолжительности фильма: Продолжительность" +
                        " фильма должна быть положителным числом");
            }
            film.setDuration(updateFilmDto.getDuration());
            log.trace("Длительность корректная");
        }
        if (updateFilmDto.hasFilmGenres()) {
            log.trace("Проверка существования жанров фильма");
            if (isGenreNonExist(updateFilmDto.getGenres())) {
                log.warn("Жанр с указанным id не найден");
                throw new InvalidFormatException("Один из жанров не найден");
            }
            log.trace("Жанры существуют");
            film.getFilmGenres().forEach(el -> filmStorage.deleteFilmGenre(updateFilmDto.getId(), el.getId()));
            updateFilmDto.setGenres(updateFilmDto.getGenres().stream().distinct().collect(Collectors.toList()));
            filmStorage.addFilmGenre(film.getId(), updateFilmDto.getGenres().stream().map(FilmGenre::getId).toList());
            film.setFilmGenres(filmStorage.getFilmGenresByFilmId(film.getId()));
        }
        if (updateFilmDto.hasFilmRating()) {
            log.trace("Проверка существования рейтинга фильма");
            if (ratingStorage.getFilmRatingById(updateFilmDto.getRate()).isEmpty()) {
                log.warn("Рейтинг с id = " + updateFilmDto.getRate() + " не найден");
                throw new InvalidFormatException("Рейтинг с id = " + updateFilmDto.getRate() + " не существует");
            }
            film.setRatingId(updateFilmDto.getRate());
        }
        log.trace("Валидация прошла успешно");
        if (updateFilmDto.hasDescription()) {
            film.setDescription(updateFilmDto.getDescription());
        }
        if (updateFilmDto.hasName()) {
            film.setName(updateFilmDto.getName());
        }
        film = filmStorage.update(film);
        log.trace("Данные о фильме успешно обновленны");
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto getFilmById(Long id) {
        log.info("Получаем фильм с id = " + id);
        log.trace("Проверка на существования фильма с указынным id");
        if (filmStorage.getFilmById(id).isEmpty()) {
            log.warn("Фильм с указанным id не найден");
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        log.trace("Фильм существует");
        Film filmFromBd = filmStorage.getFilmById(id).get();
        filmFromBd.setFilmGenres(filmStorage.getFilmGenresByFilmId(filmFromBd.getId()));
        FilmDto film = FilmMapper.mapToFilmDto(filmFromBd);
        film.setMpa(ratingStorage.getFilmRatingById(film.getMpa().getId()).get());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            film.getGenres().forEach(g -> g.setName(genreStorage.getGenreById(g.getId()).get().getName()));
        }
        return film;
    }

    public void deleteFilmById(Long id) {
        if (!filmStorage.deleteFilmById(id)) {
            log.warn("Фильм не был удален: фильм с указанным id не существует");
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
    }

    public List<FilmDto> getPopularFilms(int count) {
        log.info("Получаем топ " + count + " фильмов по оценкам пользователей");
        return filmStorage.getPopularFilm(count).stream().map(FilmMapper::mapToFilmDto).collect(Collectors.toList());
    }

    public FilmDto addFavoriteFilm(Long filmId, Long userId) {
        log.info("Пользователь с id = " + userId + " ставит лайк фильму с id = " + filmId);
        log.trace("Проверка существование фильма и пользователя");
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.warn("Фильм с указанным id не найден");
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Пользователь с указанным id не найден");
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        log.trace("Проверка прошла успешно");
        log.trace("Проверка наличия оценки данного пользователя данного фильма");
        if (filmStorage.getFavoriteFilmById(filmId, userId).isPresent()) {
            log.warn("Пользователь " + userId + " уже оценил  фильм " + filmId);
            throw new InvalidFormatException("Нельзя оценить фильм дважды");
        }
        log.trace("Пользователь еще не оценивал фильм");
        filmStorage.addFavoriteFilm(filmStorage.getFilmById(filmId).get(), userStorage.getUserById(userId).get());
        log.trace("Оценка успешно добавленна");
        return FilmMapper.mapToFilmDto(filmStorage.getFilmById(filmId).get());
    }

    public void deleteFavoriteFilm(Long filmId, Long userId) {
        log.info("Пользователь с id = " + userId + " удаляет лайк фильму с id = " + filmId);
        log.trace("Проверка существование фильма и пользователя");
        if (filmStorage.getFilmById(filmId).isEmpty()) {
            log.warn("Фильм с указанным id не найден");
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        if (userStorage.getUserById(userId).isEmpty()) {
            log.warn("Пользователь с указанным id не найден");
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        log.trace("Проверка прошла успешно");
        log.trace("Если оценка была, то она успешно удалена");
        filmStorage.deleteFavoriteFilm(filmStorage.getFilmById(filmId).get(), userStorage.getUserById(userId).get());
    }

    private boolean isBeforeMinDate(LocalDate date) {
        return (MIN_DATA.isAfter(date));
    }

    private boolean isNegativeDuration(Duration duration) {
        return !duration.isPositive();
    }

    private boolean isGenreNonExist(List<FilmGenre> genres) {
        return genres.stream().map(FilmGenre::getId).anyMatch(g -> genreStorage.getGenreById(g).isEmpty());
    }

    private boolean isRatingNonExist(FilmRating filmRating) {
        return ratingStorage.getFilmRatingById(filmRating.getId()).isEmpty();
    }
}
