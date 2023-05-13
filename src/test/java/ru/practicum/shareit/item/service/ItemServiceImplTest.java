package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.dto.*;
import ru.practicum.shareit.item.model.entity.Comment;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@DisplayName("ItemService tests")
@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    @DisplayName("'create' should create item successfully")
    void createItem_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemDto itemDto = createItemDto(item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemResponseDto actualItem = itemService.create(user.getId(), itemDto);

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(item.getId()));
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(actualItem.getRequestId(), equalTo(item.getRequest().getId()));
    }

    @Test
    @DisplayName("'create' should throw exception when user not found")
    void createItem_UserNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemDto itemDto = createItemDto(item);
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.create(id, itemDto));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'create' should throw exception when item request not found")
    void createItem_ItemRequestNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemDto itemDto = createItemDto(item);
        Long id = 1L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.create(user.getId(), itemDto));

        assertEquals(String.format("Request with ID: %d not found.", id), exception.getMessage());
        verify(itemRequestRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'update' should update item successfully")
    void updateItem_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        String newName = "Updated name";
        String newDescription = "Updated description";
        Boolean newAvailable = false;

        ItemDto updatedItemDto = ItemDto.builder()
                .id(item.getId())
                .name(newName)
                .description(newDescription)
                .available(newAvailable)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemResponseDto actualItem = itemService.update(user.getId(), updatedItemDto, item.getId());

        assertNotNull(actualItem);
        assertThat(actualItem.getName(), equalTo(newName));
        assertThat(actualItem.getDescription(), equalTo(newDescription));
        assertThat(actualItem.getAvailable(), equalTo(newAvailable));
    }

    @Test
    @DisplayName("'update' should not update item name")
    void updateItemEmptyName_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        String newName = " ";
        String newDescription = "Updated description";
        Boolean newAvailable = false;

        ItemDto updatedItemDto = ItemDto.builder()
                .id(item.getId())
                .name(newName)
                .description(newDescription)
                .available(newAvailable)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemResponseDto actualItem = itemService.update(user.getId(), updatedItemDto, item.getId());

        assertNotNull(actualItem);
        assertThat(actualItem.getName(), equalTo(item.getName()));
        assertThat(actualItem.getDescription(), equalTo(newDescription));
        assertThat(actualItem.getAvailable(), equalTo(newAvailable));
    }

    @Test
    @DisplayName("'update' should not update item description")
    void updateItemDescriptionNull_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        String newName = "Updated name";
        Boolean newAvailable = false;

        ItemDto updatedItemDto = ItemDto.builder()
                .id(item.getId())
                .name(newName)
                .description(null)
                .available(newAvailable)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemResponseDto actualItem = itemService.update(user.getId(), updatedItemDto, item.getId());

        assertNotNull(actualItem);
        assertThat(actualItem.getName(), equalTo(newName));
        assertThat(actualItem.getDescription(), equalTo(item.getDescription()));
        assertThat(actualItem.getAvailable(), equalTo(newAvailable));
    }

    @Test
    @DisplayName("'update' should not update item available")
    void updateItemAvailableNull_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        String newName = "Updated name";
        String newDescription = "Updated description";

        ItemDto updatedItemDto = ItemDto.builder()
                .id(item.getId())
                .name(newName)
                .description(newDescription)
                .available(null)
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemResponseDto actualItem = itemService.update(user.getId(), updatedItemDto, item.getId());

        assertNotNull(actualItem);
        assertThat(actualItem.getName(), equalTo(newName));
        assertThat(actualItem.getDescription(), equalTo(newDescription));
        assertThat(actualItem.getAvailable(), equalTo(item.getAvailable()));
    }

    @Test
    @DisplayName("'update' should throw exception when user not found")
    void updateItem_UserNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemDto itemDto = createItemDto(item);
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.update(id, itemDto, item.getId()));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'update' should throw exception when item not found")
    void updateItem_ItemNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemDto itemDto = createItemDto(item);
        Long id = 2L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.update(user.getId(), itemDto, id));

        assertEquals(String.format("Item with ID: %d not found.", id), exception.getMessage());
        verify(itemRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'update' should throw exception when user not owner")
    void updateItem_UserNotOwner() {
        User owner = createUser1();
        User notOwner = createUser2();
        ItemRequest itemRequest = createItemRequest1(owner);
        Item item = createItem1(owner, itemRequest);
        ItemDto itemDto = createItemDto(item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class, () ->
                itemService.update(notOwner.getId(), itemDto, itemDto.getId()));

        assertEquals(("Only the owner can edit item."), exception.getMessage());
    }

    @Test
    @DisplayName("'getAll' should return all items")
    void getAllItems_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item1 = createItem1(user, itemRequest);
        List<Item> items = List.of(item1);
        Booking lastBooking = createBooking1(user, item1);
        Booking nextBooking = createBooking2(user, item1);
        ItemResponseDto itemResponseDto1 = ItemMapper.toItemResponseDto(item1);

        itemResponseDto1.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        itemResponseDto1.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));

        List<ItemResponseDto> expectedItems = List.of(itemResponseDto1);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemIdInAndStatusOrderByStartAsc(anyList(), any()))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(itemRepository.findItemsByOwnerIdOrderByIdAsc(anyLong(), any(Pageable.class)))
                .thenReturn(items);

        List<ItemResponseDto> actualItems = itemService.getAll(user.getId(), 0, 10);

        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(expectedItems.size()));
        assertThat(actualItems.get(0).getName(), equalTo(expectedItems.get(0).getName()));
        assertThat(actualItems.get(0).getDescription(), equalTo(expectedItems.get(0).getDescription()));
        assertThat(actualItems.get(0).getAvailable(), equalTo(expectedItems.get(0).getAvailable()));
        assertThat(actualItems.get(0).getRequestId(), equalTo(expectedItems.get(0).getRequestId()));
        assertThat(actualItems.get(0).getDescription(), equalTo(expectedItems.get(0).getDescription()));
        assertThat(actualItems.get(0).getLastBooking().getId(), equalTo(expectedItems.get(0).getLastBooking().getId()));
        assertThat(actualItems.get(0).getNextBooking().getId(), equalTo(expectedItems.get(0).getNextBooking().getId()));
        assertThat(actualItems.get(0).getComments(), equalTo(List.of()));
    }

    @Test
    @DisplayName("'getAll' should throw exception when user not found")
    void getAllItem_UserNotFound() {
        Long id = 2L;

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.getAll(id, 0, 10));

        assertEquals(String.format("User with ID: %d not found.", id), exception.getMessage());
        verify(userRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("'getById' should return item by ID")
    void getItemById_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        Booking lastBooking = createBooking1(user, item);
        Booking nextBooking = createBooking2(user, item);
        ItemResponseDto expectedItem = ItemMapper.toItemResponseDto(item);

        expectedItem.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        expectedItem.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstBookingByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(lastBooking);
        when(bookingRepository.findFirstBookingByItemIdAndStartAfterAndStatusOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(nextBooking);

        ItemResponseDto actualItem = itemService.getById(user.getId(), item.getId());

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(expectedItem.getId()));
        assertThat(actualItem.getName(), equalTo(expectedItem.getName()));
        assertThat(actualItem.getDescription(), equalTo(expectedItem.getDescription()));
        assertThat(actualItem.getRequestId(), equalTo(expectedItem.getRequestId()));
        assertThat(actualItem.getAvailable(), equalTo(expectedItem.getAvailable()));
        assertThat(actualItem.getLastBooking().getId(), equalTo(expectedItem.getLastBooking().getId()));
        assertThat(actualItem.getNextBooking().getId(), equalTo(expectedItem.getNextBooking().getId()));
        assertThat(actualItem.getComments(), equalTo(List.of()));
    }

    @Test
    @DisplayName("'getById' should return item without last and next booking when bookings not found")
    void getItemById_BookingsNotFound() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        ItemResponseDto expectedItem = ItemMapper.toItemResponseDto(item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemResponseDto actualItem = itemService.getById(user.getId(), item.getId());

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(expectedItem.getId()));
        assertThat(actualItem.getName(), equalTo(expectedItem.getName()));
        assertThat(actualItem.getDescription(), equalTo(expectedItem.getDescription()));
        assertThat(actualItem.getRequestId(), equalTo(expectedItem.getRequestId()));
        assertThat(actualItem.getAvailable(), equalTo(expectedItem.getAvailable()));
        assertThat(actualItem.getLastBooking(), equalTo(expectedItem.getLastBooking()));
        assertThat(actualItem.getNextBooking(), equalTo(expectedItem.getNextBooking()));
        assertThat(actualItem.getComments(), equalTo(List.of()));
    }

    @Test
    @DisplayName("'getById' should return item without last or next bookings when user not owner")
    void getItemById_UserNotOwner() {
        User user1 = createUser1();
        User user2 = createUser2();
        ItemRequest itemRequest = createItemRequest1(user1);
        Item item = createItem1(user1, itemRequest);
        ItemResponseDto expectedItem = ItemMapper.toItemResponseDto(item);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemResponseDto actualItem = itemService.getById(user2.getId(), item.getId());

        assertNotNull(actualItem);
        assertThat(actualItem.getId(), equalTo(expectedItem.getId()));
        assertThat(actualItem.getName(), equalTo(expectedItem.getName()));
        assertThat(actualItem.getDescription(), equalTo(expectedItem.getDescription()));
        assertThat(actualItem.getRequestId(), equalTo(expectedItem.getRequestId()));
        assertThat(actualItem.getAvailable(), equalTo(expectedItem.getAvailable()));
        assertThat(actualItem.getLastBooking(), equalTo(null));
        assertThat(actualItem.getNextBooking(), equalTo(null));
        assertThat(actualItem.getComments(), equalTo(List.of()));
    }

    @Test
    @DisplayName("'getById' should throw exception when user not found")
    void getItemById_UserNotFound() {
        Long userId = 2L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.getById(userId, anyLong()));

        assertEquals(String.format("User with ID: %d not found.", userId), exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getById' should throw exception when item not found")
    void getItemById_ItemNotFound() {
        User user = createUser1();
        Long itemId = 2L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.getById(user.getId(), itemId));
        assertEquals(String.format("Item with ID: %d not found.", itemId), exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("'searchItem' should return all items by search criteria")
    void searchItem_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item1 = createItem1(user, itemRequest);
        Item item2 = createItem2(user, itemRequest);
        List<Item> items = List.of(item1, item2);
        List<ItemResponseDto> expectedItems = ItemMapper.toItemResponseDto(items);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.search(any(), any(Pageable.class)))
                .thenReturn(items);

        List<ItemResponseDto> actualItems = itemService.searchItem(user.getId(), "Item", 0, 10);

        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(expectedItems.size()));
        assertThat(actualItems.get(0).getName(), equalTo(expectedItems.get(0).getName()));
        assertThat(actualItems.get(0).getDescription(), equalTo(expectedItems.get(0).getDescription()));
        assertThat(actualItems.get(0).getAvailable(), equalTo(expectedItems.get(0).getAvailable()));
        assertThat(actualItems.get(0).getRequestId(), equalTo(expectedItems.get(0).getRequestId()));
        assertThat(actualItems.get(0).getDescription(), equalTo(expectedItems.get(0).getDescription()));
        assertThat(actualItems.get(0).getLastBooking(), equalTo(expectedItems.get(0).getLastBooking()));
        assertThat(actualItems.get(0).getNextBooking(), equalTo(expectedItems.get(0).getNextBooking()));
        assertThat(actualItems.get(0).getComments(), equalTo(expectedItems.get(0).getComments()));
    }

    @Test
    @DisplayName("'searchItem' should throw exception when user not found")
    void searchItem_UserNotFound() {
        Long userId = 2L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.searchItem(userId, "Item", 0, 10));

        assertEquals(String.format("User with ID: %d not found.", userId), exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("'addComment' should create comment successfully")
    void createComment_Success() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        CommentDto commentDto = createCommentDto();
        Comment comment = createComment(commentDto, item, user);
        CommentResponseDto expectedComment = createCommentResponseDto(commentDto, user);
        Boolean isExist = true;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), any(), any(), any()))
                .thenReturn(isExist);

        CommentResponseDto actualComment = itemService.addComment(user.getId(), item.getId(), commentDto);

        assertNotNull(actualComment);
        assertThat(actualComment.getId(), equalTo(expectedComment.getId()));
        assertThat(actualComment.getText(), equalTo(expectedComment.getText()));
        assertThat(actualComment.getAuthorName(), equalTo(expectedComment.getAuthorName()));
    }

    @Test
    @DisplayName("'addComment' should throw exception when user not found")
    void createComment_UserNotFound() {
        CommentDto commentDto = createCommentDto();
        Long itemId = 1L;
        Long userId = 2L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.addComment(userId, itemId, commentDto));

        assertEquals(String.format("User with ID: %d not found.", userId), exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("'addComment' should throw exception when item not found")
    void createComment_ItemNotFound() {
        User user = createUser1();
        CommentDto commentDto = createCommentDto();
        Long itemId = 2L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () ->
                itemService.addComment(user.getId(), itemId, commentDto));
        assertEquals(String.format("Item with ID: %d not found.", itemId), exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    @DisplayName("'addComment' should throw exception when item isn't booked by user")
    void createComment_UserNotBookedItem() {
        User user = createUser1();
        ItemRequest itemRequest = createItemRequest1(user);
        Item item = createItem1(user, itemRequest);
        CommentDto commentDto = createCommentDto();
        Boolean isExist = false;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), any(), any(), any()))
                .thenReturn(isExist);

        NotAvailableException exception = assertThrows(NotAvailableException.class, () ->
                itemService.addComment(user.getId(), item.getId(), commentDto));
        assertEquals(("You haven't booked this item yet."), exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
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
                .email("user2Email@mail.com")
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

    private Item createItem2(User owner, ItemRequest itemRequest) {
        return Item.builder()
                .id(2L)
                .name("Item 2 name")
                .description("Item 2 description")
                .owner(owner)
                .available(true)
                .request(itemRequest)
                .build();
    }

    private ItemDto createItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .text("Comment 1")
                .build();
    }

    private Comment createComment(CommentDto commentDto, Item item, User author) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .build();
    }

    private CommentResponseDto createCommentResponseDto(CommentDto commentDto, User author) {
        return CommentResponseDto.builder()
                .id(1L)
                .text(commentDto.getText())
                .authorName(author.getName())
                .build();
    }

    private Booking createBooking1(User user, Item item) {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .status(BookingStatus.APPROVED)
                .booker(user)
                .item(item)
                .build();
    }

    private Booking createBooking2(User user, Item item) {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(BookingStatus.APPROVED)
                .booker(user)
                .item(item)
                .build();
    }
}