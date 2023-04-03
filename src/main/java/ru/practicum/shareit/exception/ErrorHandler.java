package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

@Slf4j
public class ErrorHandler {
    private static final String ERROR = "error";

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentEx(final RuntimeException e) {
        log.warn("Некорректное значение.");
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler({ObjectDoesNotExist.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleObjectDoesNotExistException(final ObjectDoesNotExist e) {
        log.warn("Запрашиваемый объект не найден.");
        return Map.of(ERROR, e.getMessage());
    }
}
