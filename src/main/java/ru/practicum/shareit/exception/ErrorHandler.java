package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final String ERROR = "error";

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentEx(final RuntimeException e) {
        log.warn("Некорректное значение.");
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(final NoSuchElementException e) {
        log.warn("Запрашиваемый объект не найден.");
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({DuplicateEmailException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDuplicateEmailException(final DuplicateEmailException e) {
        log.warn("Пользователь с таким email уже существует.");
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({PermissionDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handlePermissionDeniedException(final PermissionDeniedException e) {
        log.warn("У вас нет прав на редактирование.");
        return Map.of(ERROR, e.getMessage());
    }
}
