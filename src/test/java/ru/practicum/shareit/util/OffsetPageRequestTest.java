package ru.practicum.shareit.util;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.util.pagination.OffsetPageRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OffsetPageRequestTest {

    @Test
    public void testGetOffset() {
        int from = 20;
        int size = 10;

        OffsetPageRequest pageRequest = new OffsetPageRequest(from, size);

        assertEquals(from, pageRequest.getOffset());
        assertEquals(from / size, pageRequest.getPageNumber());
        assertEquals(size, pageRequest.getPageSize());
        assertEquals(Sort.unsorted(), pageRequest.getSort());
    }
}
