package ru.practicum.shareit.util.validator.pagination;

public class PaginationValidator {
    public static void validateSize(Integer size) {
        if (size == 0) {
            throw new IllegalArgumentException("Size can't be 0.");
        }
    }
}
