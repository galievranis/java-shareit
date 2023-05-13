package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.pagination.OffsetPageRequest;
import ru.practicum.shareit.util.validator.pagination.PaginationValidator;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest), null);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NoSuchElementException(String.format("Request with ID: %d not found.", requestId)));
        List<Item> items = itemRepository.findItemsByRequestId(requestId);
        return ItemRequestMapper.toItemRequestDto(itemRequest, ItemMapper.toItemResponseShortDto(items));
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, Integer from, Integer size) {
        getUserById(userId);
        PaginationValidator.validate(from, size);
        Pageable pageable = OffsetPageRequest.of(from, size);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable).stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .collect(Collectors.toList());

        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findItemsByRequestIdIn(itemRequestIds);

        return ItemRequestMapper.toItemRequestDto(itemRequests, ItemMapper.toItemResponseShortDto(items));
    }

    @Override
    public List<ItemRequestDto> getAllOwn(Long userId, Integer from, Integer size) {
        getUserById(userId);
        PaginationValidator.validate(from, size);
        Pageable pageable = OffsetPageRequest.of(from, size);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId, pageable).stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated))
                .collect(Collectors.toList());

        List<Long> itemRequestIds = itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemRepository.findItemsByRequestIdIn(itemRequestIds);

        return ItemRequestMapper.toItemRequestDto(itemRequests, ItemMapper.toItemResponseShortDto(items));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("User with ID: %d not found.", id)));
    }
}
