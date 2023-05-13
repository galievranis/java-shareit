package ru.practicum.shareit.util.validator.pagination;

public class PaginationValidator {
    public static void validate(Integer from, Integer size) {
        if (from < 0) {
            throw new IllegalArgumentException("From can't be negative.");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Size can't be negative or equals to 0.");
        }
    }
}
