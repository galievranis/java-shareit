package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@DisplayName("ItemRequestController tests")
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("'create' should create item request successfully")
    public void createItemRequest_Success() throws Exception {
        User requestor = createUser1();
        ItemRequest itemRequest = createItemRequest1(requestor);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);

        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(OWNER_ID_HEADER, requestor.getId())
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    @DisplayName("'getAll' should return all item requests successfully")
    void getAllItemRequests_Success() throws Exception {
        User requestor = createUser1();
        ItemRequest itemRequest1 = createItemRequest1(requestor);
        ItemRequest itemRequest2 = createItemRequest2(requestor);
        ItemRequestDto itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1, null);
        ItemRequestDto itemRequestDto2 = ItemRequestMapper.toItemRequestDto(itemRequest2, null);
        List<ItemRequestDto> itemRequestDtos = List.of(itemRequestDto1, itemRequestDto2);

        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequestDtos);

        mvc.perform(get("/requests/all")
                        .header(OWNER_ID_HEADER, requestor.getId())
                        .content(objectMapper.writeValueAsString(itemRequestDtos))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].items", is(itemRequestDto1.getItems())))
                .andExpect(jsonPath("$[1].id", is(itemRequestDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(itemRequestDto2.getDescription())))
                .andExpect(jsonPath("$[1].items", is(itemRequestDto2.getItems())));
    }

    @Test
    @DisplayName("'getAllOwn' should return all own item requests successfully")
    void getAllOwnItemRequests_Success() throws Exception {
        User requestor1 = createUser1();
        ItemRequest itemRequest1 = createItemRequest1(requestor1);
        ItemRequestDto itemRequestDto1 = ItemRequestMapper.toItemRequestDto(itemRequest1, null);
        List<ItemRequestDto> itemRequestDtos1 = List.of(itemRequestDto1);

        when(itemRequestService.getAllOwn(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequestDtos1);

        mvc.perform(get("/requests")
                        .header(OWNER_ID_HEADER, requestor1.getId())
                        .content(objectMapper.writeValueAsString(itemRequestDtos1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemRequestDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(itemRequestDto1.getDescription())))
                .andExpect(jsonPath("$[0].items", is(itemRequestDto1.getItems())));
    }

    @Test
    @DisplayName("'getById' should return item request successfully")
    void getItemRequestById_Success() throws Exception {
        User requestor = createUser1();
        ItemRequest itemRequest = createItemRequest1(requestor);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);

        when(itemRequestService.getById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", itemRequestDto.getId())
                        .header(OWNER_ID_HEADER, requestor.getId())
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.items", is(itemRequestDto.getItems())));
    }

    private User createUser1() {
        return User.builder()
                .id(1L)
                .name("User 1")
                .email("user1Email@mail.com")
                .build();
    }

    private ItemRequest createItemRequest1(User requestor) {
        return ItemRequest.builder()
                .id(1L)
                .description("Item request 1 description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    private ItemRequest createItemRequest2(User requestor) {
        return ItemRequest.builder()
                .id(2L)
                .description("Item request 2 description")
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }
}