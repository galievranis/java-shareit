package ru.practicum.shareit.exception;

/**
 * Исключение для случаев, когда отказано в доступе на редактирование
 */

public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException(String message) {
        super(message);
    }
}
