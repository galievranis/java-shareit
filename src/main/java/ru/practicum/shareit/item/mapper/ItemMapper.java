package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.dto.ItemResponseDto;
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

    public List<ItemResponseDto> toItemResponseDto(Iterable<Item> items) {
        List<ItemResponseDto> result = new ArrayList<>();

        for (Item item : items) {
            result.add(toItemResponseDto(item));
        }

        return result;
    }
}
