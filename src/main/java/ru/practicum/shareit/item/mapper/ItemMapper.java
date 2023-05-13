package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.dto.ItemResponseShortDto;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.user.model.entity.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class ItemMapper {

    public ItemResponseDto toItemResponseDto(Item item) {
        return ItemResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    public ItemResponseShortDto toItemResponseShortDto(Item item) {
        return ItemResponseShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public List<ItemResponseDto> toItemResponseDto(Iterable<Item> items) {
        List<ItemResponseDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toItemResponseDto(item));
        }

        return result;
    }

    public List<ItemResponseShortDto> toItemResponseShortDto(Iterable<Item> items) {
        List<ItemResponseShortDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toItemResponseShortDto(item));
        }

        return result;
    }
}
