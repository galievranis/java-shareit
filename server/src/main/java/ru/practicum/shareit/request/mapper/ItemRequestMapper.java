package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.dto.ItemResponseShortDto;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.request.model.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.entity.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<ItemResponseShortDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(LocalDateTime.now())
                .build();
    }

    public List<ItemRequestDto> toItemRequestDto(Iterable<ItemRequest> itemRequests, List<ItemResponseShortDto> items) {
        List<ItemRequestDto> result = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            result.add(toItemRequestDto(itemRequest, items));
        }

        return result;
    }
}
