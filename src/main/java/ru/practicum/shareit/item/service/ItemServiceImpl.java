package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.PermissionDeniedException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
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
    public ItemResponseDto create(Long userId, ItemDto itemDto) {
        User owner = UserMapper.toUser(userService.getById(userId));
        Item item = ItemMapper.toItem(itemDto, owner);
        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long userId, ItemDto itemDto, Long itemId) {
        userService.getById(userId);
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(
                    String.format("Item with ID: %d not found.", itemId)));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("Only the owner can edit item.");
        }

        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            itemToUpdate.setName(itemDto.getName());
        }

        if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }

        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemResponseDto(itemToUpdate);
    }

    @Override
    public List<ItemResponseDto> getAll(Long userId) {
        userService.getById(userId);
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId);

        List<Long> itemIds = itemList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        Map<Long, List<Booking>> bookingsByItem = bookingRepository
                .findAllByItemIdInAndStatusOrderByStartAsc(itemIds, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));

        Sort sortByCreatedDesc = Sort.by(Sort.Direction.DESC, "created");
        Map<Long, List<Comment>> commentsByItem = commentRepository
                .findAllByItemIn(itemList, sortByCreatedDesc)
                .stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        List<ItemResponseDto> itemDtoList = new ArrayList<>();

        for (Item item : itemList) {
            List<Booking> bookings = bookingsByItem.getOrDefault(item.getId(), List.of());
            ItemResponseDto itemDto = ItemMapper.toItemResponseDto(item);
            LocalDateTime now = LocalDateTime.now();

            if (!bookings.isEmpty()) {
                Booking lastBooking = bookings.stream()
                        .filter(booking -> !booking.getStart().isAfter(now))
                        .reduce((first, second) -> second)
                        .orElse(null);
                Booking nextBooking = bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(now))
                        .findFirst()
                        .orElse(null);
                itemDto.setLastBooking(lastBooking != null ? BookingMapper.toBookingShortDto(lastBooking) : null);
                itemDto.setNextBooking(nextBooking != null ? BookingMapper.toBookingShortDto(nextBooking) : null);
            }

            List<CommentResponseDto> comments = CommentMapper.toCommentResponseDto(commentsByItem.getOrDefault(item.getId(), List.of()));
            itemDto.setComments(comments);
            itemDtoList.add(itemDto);
        }

        return itemDtoList;
    }

    @Override
    public ItemResponseDto getById(Long userId, Long itemId) {
        userService.getById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", itemId)));
        ItemResponseDto itemResponseDto = ItemMapper.toItemResponseDto(item);
        itemResponseDto.setComments(getCommentsByItemId(item.getId()));

        if (!item.getOwner().getId().equals(userId)) {
            return itemResponseDto;
        }

        LocalDateTime now = LocalDateTime.now();
        setLastBooking(itemResponseDto, now);
        setNextBooking(itemResponseDto, now);
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> searchItem(Long userId, String searchCriteria) {
        userService.getById(userId);
        List<Item> items = itemRepository.search(searchCriteria);
        return ItemMapper.toItemResponseDto(items);
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", itemId)));
        User user = UserMapper.toUser(userService.getById(userId));
        Boolean isExists = bookingRepository.existsByBookerIdAndItemIdAndStatusAndEndBefore(
                userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());

        if (!isExists) {
            throw new NotAvailableException("You haven't booked this item yet.");
        }

        Comment comment = CommentMapper.toComment(commentDto, item, user);
        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    private void setLastBooking(ItemResponseDto itemResponseDto, LocalDateTime dateTime) {
        Booking lastBooking = bookingRepository.findFirstBookingByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
                itemResponseDto.getId(), dateTime, BookingStatus.APPROVED);

        if (lastBooking != null) {
            itemResponseDto.setLastBooking(BookingMapper.toBookingShortDto(lastBooking));
        } else {
            itemResponseDto.setLastBooking(null);
        }
    }

    private void setNextBooking(ItemResponseDto itemResponseDto, LocalDateTime dateTime) {
        Booking nextBooking = bookingRepository.findFirstBookingByItemIdAndStartAfterAndStatusOrderByStartAsc(
                itemResponseDto.getId(), dateTime, BookingStatus.APPROVED);

        if (nextBooking != null) {
            itemResponseDto.setNextBooking(BookingMapper.toBookingShortDto(nextBooking));
        } else {
            itemResponseDto.setNextBooking(null);
        }
    }

    private List<CommentResponseDto> getCommentsByItemId(Long itemId) {
        return CommentMapper.toCommentResponseDto(commentRepository.findAllByItemId(itemId));
    }
}
