package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.user.model.entity.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.constants.RequestHeaderConstants.OWNER_ID_HEADER;

@DisplayName("ItemController tests")
@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("'create' should create item successfully'")
    public void createItem_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);

        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(itemResponseDto);

        mvc.perform(post("/items")
                        .header(OWNER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(itemResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemResponseDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemResponseDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemResponseDto.getComments())));
    }

    @Test
    @DisplayName("'update' should update item successfully'")
    public void updateItem_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);

        when(itemService.update(anyLong(), any(ItemDto.class), anyLong()))
                .thenReturn(itemResponseDto);

        mvc.perform(patch("/items/{itemId}", itemResponseDto.getId())
                        .header(OWNER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(itemResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemResponseDto.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemResponseDto.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemResponseDto.getComments())));
    }

    @Test
    @DisplayName("'getAll' should return all items successfully")
    public void getAllItemsByUserId_Success() throws Exception {
        User user1 = createUser1();
        ItemRequest itemRequest = createItemRequest1(user1);
        Item item1 = createItem1(user1, itemRequest);
        ItemResponseDto itemResponseDto1 = ItemMapper.toItemResponseDto(item1);
        List<ItemResponseDto> items = List.of(itemResponseDto1);

        when(itemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(items);

        mvc.perform(get("/items")
                        .header(OWNER_ID_HEADER, user1.getId())
                        .content(objectMapper.writeValueAsString(items))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto1.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemResponseDto1.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemResponseDto1.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemResponseDto1.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemResponseDto1.getComments())));
    }

    @Test
    @DisplayName("'getById' should return item by id successfully")
    public void getItemById_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item1 = createItem1(user, itemRequest);
        ItemResponseDto itemResponseDto1 = ItemMapper.toItemResponseDto(item1);

        when(itemService.getById(anyLong(), anyLong()))
                .thenReturn(itemResponseDto1);

        mvc.perform(get("/items/{itemId}", item1.getId())
                        .header(OWNER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(itemResponseDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemResponseDto1.getName())))
                .andExpect(jsonPath("$.description", is(itemResponseDto1.getDescription())))
                .andExpect(jsonPath("$.available", is(itemResponseDto1.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemResponseDto1.getRequestId()), Long.class))
                .andExpect(jsonPath("$.lastBooking", is(itemResponseDto1.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemResponseDto1.getNextBooking())))
                .andExpect(jsonPath("$.comments", is(itemResponseDto1.getComments())));
    }

    @Test
    @DisplayName("'search' should return all items by search criteria successfully")
    public void getALlItemsBySearchCriteria_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item1 = createItem1(user, itemRequest);
        ItemResponseDto itemResponseDto1 = ItemMapper.toItemResponseDto(item1);

        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemResponseDto1));

        mvc.perform(get("/items/search")
                        .header(OWNER_ID_HEADER, user.getId())
                        .param("text", "Item")
                        .content(objectMapper.writeValueAsString(itemResponseDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemResponseDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(itemResponseDto1.getName())))
                .andExpect(jsonPath("$[0].description", is(itemResponseDto1.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemResponseDto1.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(itemResponseDto1.getRequestId()), Long.class))
                .andExpect(jsonPath("$[0].lastBooking", is(itemResponseDto1.getLastBooking())))
                .andExpect(jsonPath("$[0].nextBooking", is(itemResponseDto1.getNextBooking())))
                .andExpect(jsonPath("$[0].comments", is(itemResponseDto1.getComments())));
    }

    @Test
    @DisplayName("'search' should return empty list when search criteria is empty")
    public void getALlItemsBySearchCriteria_SearchCriteriaIsEmpty() throws Exception {
        User user = createUser1();

        when(itemService.searchItem(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

        mvc.perform(get("/items/search")
                        .header(OWNER_ID_HEADER, user.getId())
                        .param("text", " ")
                        .content(objectMapper.writeValueAsString(List.of()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(empty())));
    }

    @Test
    @DisplayName("'addComment' should create comment successfully")
    public void createComment_Success() throws Exception {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        CommentDto commentDto = createCommentDto();
        CommentResponseDto commentResponseDto = createCommentResponseDto(commentDto, user);

        when(itemService.addComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", item.getId())
                        .header(OWNER_ID_HEADER, user.getId())
                        .content(objectMapper.writeValueAsString(commentResponseDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentResponseDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentResponseDto.getAuthorName())));
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

    private Item createItem1(User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(1L)
                .name("Item 1 name")
                .description("Item 1 description")
                .owner(owner)
                .available(true)
                .request(itemRequest)
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Comment 1")
                .build();
    }

    private CommentResponseDto createCommentResponseDto(CommentDto commentDto, User author) {
        return CommentResponseDto.builder()
                .id(1L)
                .text(commentDto.getText())
                .authorName(author.getName())
                .build();
    }
}
