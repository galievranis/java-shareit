package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pagination.OffsetPageRequest;
import ru.practicum.shareit.user.model.entity.User;
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
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    @Transactional
    public ItemResponseDto create(Long userId, ItemDto itemDto) {
        User owner = getUserById(userId);
        Item item = ItemMapper.toItem(itemDto, owner);

        if (itemDto.getRequestId() != null) {
            Long requestId = itemDto.getRequestId();
            ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                    new NoSuchElementException(String.format("Request with ID: %d not found.", requestId)));
            item.setRequest(itemRequest);
        }

        return ItemMapper.toItemResponseDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemResponseDto update(Long userId, ItemDto itemDto, Long itemId) {
        getUserById(userId);
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
    public List<ItemResponseDto> getAll(Long userId, Integer from, Integer size) {
        getUserById(userId);
        Pageable pageable = OffsetPageRequest.of(from, size);
        List<Item> itemList = itemRepository.findItemsByOwnerIdOrderByIdAsc(userId, pageable);

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
        getUserById(userId);
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
    public List<ItemResponseDto> searchItem(Long userId, String searchCriteria, Integer from, Integer size) {
        getUserById(userId);
        Pageable pageable = OffsetPageRequest.of(from, size);
        List<Item> items = itemRepository.search(searchCriteria, pageable);
        return ItemMapper.toItemResponseDto(items);
    }

    @Override
    @Transactional
    public CommentResponseDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        User user = getUserById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NoSuchElementException(String.format("Item with ID: %d not found.", itemId)));
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

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("User with ID: %d not found.", id)));
    }
}
