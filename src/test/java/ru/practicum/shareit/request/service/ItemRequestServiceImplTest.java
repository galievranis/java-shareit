package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("ItemRequestService tests")
@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    @DisplayName("'create' should create item request successfully")
    void createItemRequest_Success() {
        // given
        User requestor = createUser1();
        ItemRequest itemRequest = createItemRequest1(requestor);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        // when
        ItemRequestDto actualItemRequestDto = itemRequestService.create(requestor.getId(), itemRequestDto);

        // then
        assertNotNull(actualItemRequestDto);
        assertThat(actualItemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(actualItemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
    }

    @Test
    @DisplayName("'create' should throw exception when user is not found")
    void createItemRequest_UserNotFound() {
        // given
        User requestor = createUser1();
        Long id = 2L;
        ItemRequest itemRequest = createItemRequest1(requestor);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);
        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemRequestService.create(id, itemRequestDto));

        // then
        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'getById' should return item request by ID")
    void getItemRequestById_Success() {
        // given
        User requestor = createUser1();
        ItemRequest itemRequest = createItemRequest1(requestor);
        Item item = createItem(itemRequest, requestor);
        List<Item> items = List.of(item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findItemsByRequestId(anyLong()))
                .thenReturn(items);

        // when
        ItemRequestDto actualItemRequestDto = itemRequestService.getById(requestor.getId(), 1L);

        // then
        assertNotNull(actualItemRequestDto);
        assertThat(actualItemRequestDto.getId(), equalTo(itemRequest.getId()));
        assertThat(actualItemRequestDto.getDescription(), equalTo(itemRequest.getDescription()));
    }

    @Test
    @DisplayName("'getById' should throw exception when user is not found")
    void getItemRequestById_UserNotFound() {
        // given
        User requestor = createUser1();
        Long id = 2L;
        ItemRequest itemRequest = createItemRequest1(requestor);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest, null);
        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemRequestService.getById(id, itemRequestDto.getId()));

        // then
        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'getById' should throw exception when item request is not found")
    void getItemRequestById_ItemRequestNotFound() {
        // given
        User requestor = createUser1();
        Long id = 2L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemRequestService.getById(requestor.getId(), id));

        // then
        assertEquals(String.format("Request with ID: %d not found.", id), exception.getMessage());
    }

    @Test
    @DisplayName("'getAll' should return all item requests")
    void getAllItemRequests_Success() {
        // given
        User requestor = createUser1();
        ItemRequest itemRequest1 = createItemRequest1(requestor);
        ItemRequest itemRequest2 = createItemRequest2(requestor);
        Item item = createItem(itemRequest1, requestor);
        List<ItemRequest> itemRequests = List.of(itemRequest1, itemRequest2);
        List<Item> items = List.of(item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(itemRequests);
        when(itemRepository.findItemsByRequestIdIn(anyList()))
                .thenReturn(items);

        // when
        List<ItemRequestDto> actualItemRequestDtos = itemRequestService.getAll(requestor.getId(), 0, 10);

        // then
        assertNotNull(actualItemRequestDtos);
        assertThat(actualItemRequestDtos.size(), equalTo(itemRequests.size()));
        verify(userRepository, times(1)).findById(requestor.getId());
    }

    @Test
    @DisplayName("'getAll' should throw exception when user is not found")
    void getAllItemRequests_UserNotFound() {
        // given
        Long id = 2L;
        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemRequestService.getAll(id, 0, 10));

        // then
        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'getAllOwn' should return all own item requests")
    void getAllOwnItemRequests_Success() {
        // given
        User owner = createUser1();
        User requestor = createUser2();
        ItemRequest itemRequest1 = createItemRequest1(requestor);
        Item item = createItem(itemRequest1, owner);
        List<ItemRequest> itemRequests = List.of(itemRequest1);
        List<Item> items = List.of(item);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(requestor));
        when(itemRepository.findItemsByRequestIdIn(anyList()))
                .thenReturn(items);
        when(itemRequestRepository.findAllByRequestorId(anyLong(), any(PageRequest.class)))
                .thenReturn(itemRequests);

        // when
        List<ItemRequestDto> actualItemRequestDtos = itemRequestService.getAllOwn(requestor.getId(), 0, 10);

        // then
        assertNotNull(actualItemRequestDtos);
        assertThat(actualItemRequestDtos.size(), equalTo(itemRequests.size()));
        verify(userRepository, times(1)).findById(requestor.getId());
    }

    @Test
    @DisplayName("'getAllOwn' should throw exception when user is not found")
    void getAllOwnItemRequests_UserNotFound() {
        // given
        Long id = 2L;
        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        // when
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemRequestService.getAllOwn(id, 0, 10));

        // then
        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    private User createUser1() {
        return User.builder()
                .id(1L)
                .name("User 1")
                .email("user1Email@mail.com")
                .build();
    }

    private User createUser2() {
        return User.builder()
                .id(2L)
                .name("User 2")
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

    private Item createItem(ItemRequest itemRequest, User owner) {
        return Item.builder()
                .id(1L)
                .name("Item name")
                .description("Item description")
                .owner(owner)
                .request(itemRequest)
                .build();
    }
}