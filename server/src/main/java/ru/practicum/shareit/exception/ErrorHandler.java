package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private static final String ERROR = "error";

    @ExceptionHandler({Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(final Exception e) {
        log.warn("Unexpected error has occurred. Error details: {}.", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({NoSuchElementException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoSuchElementException(final NoSuchElementException e) {
        log.warn("Requested object was not found. Error details: {}.", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({PermissionDeniedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePermissionDeniedException(final PermissionDeniedException e) {
        log.warn("You don't have enough permissions to edit. Error details: {}.", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("Incorrect data from the user. Error details: {}.", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({NotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotAvailableException(final NotAvailableException e) {
        log.warn("Item isn't available for booking. Error details: {}.", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentEx(final RuntimeException e) {
        log.warn("Invalid value. Error details: {}.", e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }
}
