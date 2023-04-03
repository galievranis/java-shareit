package ru.practicum.shareit.exception;

public class ObjectDoesNotExist extends RuntimeException {
    public ObjectDoesNotExist(String message) {
        super(message);
    }
}
