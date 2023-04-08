package ru.practicum.shareit.exception;

/**
 * Исключение для случаев, когда найден дубликат email
 */

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
