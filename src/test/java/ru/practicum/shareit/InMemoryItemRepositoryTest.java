package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InMemoryItemRepositoryTest {

    private final ItemController itemController;
    private final UserController userController;

    @BeforeEach
    public void beforeEach() {
        User user = User.builder()
                .name("John")
                .email("john@email.com")
                .build();

        userController.createUser(user);
    }

    @Test
    public void shouldCreateItem() {
        ItemDto itemToCreate = ItemDto.builder()
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        ItemDto expectedItem = ItemDto.builder()
                .id(1L)
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        ItemDto actualItem = itemController.createItem(1L, itemToCreate);

        assertEquals(expectedItem, actualItem, "Вещи не совпадают.");
    }

    @Test
    public void shouldUpdateItem() {
        ItemDto itemToCreate = ItemDto.builder()
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        itemController.createItem(1L, itemToCreate);

        ItemDto expectedItem = ItemDto.builder()
                .id(1L)
                .name("XBOX Series S")
                .description("Gaming console from Microsoft")
                .available(false)
                .request(null)
                .build();

        ItemDto itemToUpdate = ItemDto.builder()
                .name("XBOX Series S")
                .available(false)
                .build();

        ItemDto actualItem = itemController.updateItem(1L, itemToUpdate, 1L);

        assertEquals(expectedItem, actualItem, "Вещи не совпадают.");
    }

    @Test
    public void shouldReturnAllItemsByUserId() {
        ItemDto firstItem = ItemDto.builder()
                .id(1L)
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        ItemDto secondItem = ItemDto.builder()
                .id(2L)
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        itemController.createItem(1L, firstItem);
        itemController.createItem(1L, secondItem);

        List<ItemDto> expectedItemsList = new ArrayList<>();
        expectedItemsList.add(firstItem);
        expectedItemsList.add(secondItem);

        List<ItemDto> actualItemsList = itemController.getAllItemsByUserId(1L);

        assertEquals(expectedItemsList, actualItemsList, "Списки вещей не совпадают.");
    }

    @Test
    public void shouldReturnItemById() {
        ItemDto itemToCreate = ItemDto.builder()
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        itemController.createItem(1L, itemToCreate);

        ItemDto expectedItem = ItemDto.builder()
                .id(1L)
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        ItemDto actualItem = itemController.getItemById(1L, 1L);

        assertEquals(expectedItem, actualItem, "Вещи не совпадают.");
    }

    @Test
    public void shouldReturnItemBySearchCriteria() {
        String searchCriteria = "XBOX";

        ItemDto itemToCreate = ItemDto.builder()
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();

        itemController.createItem(1L, itemToCreate);

        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("XBOX Series X")
                .description("Gaming console from Microsoft")
                .available(true)
                .request(null)
                .build();
        List<ItemDto> expectedItemsList = new ArrayList<>();
        expectedItemsList.add(itemDto);

        List<ItemDto> actualItemsList = itemController.searchItem(1L, searchCriteria);

        assertEquals(expectedItemsList, actualItemsList, "Списки вещей не совпадают.");
    }
}
