package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.entity.Comment;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Lazy
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.getById(userId));
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, ItemDto itemDto, Long itemId) {
        userService.getById(userId);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(
                    String.format("Item with ID: %d not found.", itemId)));

        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Only the owner can edit item.");
        }

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        Item updatedItem = itemRepository.save(oldItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public List<ItemDto> getAll(Long userId) {
        userService.getById(userId);
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);

        List<Long> itemIds = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<Booking>> bookingsByItem = bookingRepository
                .findAllByItemIdInAndStatus(itemIds, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Map<Long, List<Comment>> commentsByItem = commentRepository
                .findAllByItemIn(itemList)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item : itemList) {
            List<Booking> bookings = bookingsByItem.getOrDefault(item.getId(), List.of());
            ItemDto itemDto = ItemMapper.toItemDto(item);

            if (!bookings.isEmpty()) {
                Booking lastBooking = bookings.get(0);
                Booking nextBooking = bookings.get(bookings.size() - 1);
                itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
                itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
            }

            List<CommentDto> comments = CommentMapper.toCommentDto(commentsByItem.getOrDefault(item.getId(), List.of()));
            itemDto.setComments(comments);

            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }

    @Override
    @Transactional
    public ItemDto getById(Long userId, Long itemId) {
        userService.getById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", itemId)));

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(getCommentsByItemId(item.getId()));

        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }

        setLastBooking(itemDto);
        setNextBooking(itemDto);

        return itemDto;
    }

    private void setLastBooking(ItemDto itemDto) {
        Booking lastBooking = bookingRepository.findFirstBookingByItemIdAndStartBeforeAndStatusOrderByStartDesc(
                itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        } else {
            itemDto.setLastBooking(null);
        }
    }

    private void setNextBooking(ItemDto itemDto) {
        Booking nextBooking = bookingRepository.findFirstBookingByItemIdAndStartAfterAndStatusOrderByStartAsc(
                itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED);

        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
        } else {
            itemDto.setNextBooking(null);
        }
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String searchCriteria) {
        userService.getById(userId);
        List<Item> items = itemRepository.search(searchCriteria);
        return ItemMapper.toItemDto(items);
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", itemId)));
        User user = UserMapper.toUser(userService.getById(userId));
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new NotAvailableException("You haven't booked this item yet.");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Transactional
    public List<CommentDto> getCommentsByItemId(Long itemId) {
        return CommentMapper.toCommentDto(commentRepository.findAllByItemId(itemId));
    }
}
