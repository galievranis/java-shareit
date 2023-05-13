package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.entity.Item;
import ru.practicum.shareit.request.model.entity.ItemRequest;
import ru.practicum.shareit.user.model.entity.User;
import ru.practicum.shareit.util.pagination.OffsetPageRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@DisplayName("ItemRepository tests")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @DisplayName("'search' should return list with item by search criteria successfully")
    public void returnItemsBySearchCriteria_Success() {
        User user1 = User.builder()
                .name("User 1")
                .email("user1Email@mail.ru")
                .build();

        User user2 = User.builder()
                .name("User 2")
                .email("user2Email@mail.ru")
                .build();

        em.persist(user1);
        em.persist(user2);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .requestor(user2)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .requestor(user1)
                .build();

        em.persist(itemRequest1);
        em.persist(itemRequest2);

        Item item1 = Item.builder()
                .name("XBOX Series X")
                .description("Gaming console by Microsoft")
                .owner(user1)
                .available(true)
                .request(itemRequest1)
                .build();

        Item item2 = Item.builder()
                .name("PlayStation 5")
                .description("Gaming console by SONY")
                .owner(user2)
                .available(true)
                .request(itemRequest2)
                .build();

        em.persist(item1);
        em.persist(item2);

        List<Item> actualItems = itemRepository.search("XBOX", OffsetPageRequest.of(0, 10));

        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(1));
        assertThat(actualItems.get(0).getId(), equalTo(item1.getId()));
        assertThat(actualItems.get(0).getName(), equalTo(item1.getName()));
        assertThat(actualItems.get(0).getDescription(), equalTo(item1.getDescription()));
        assertThat(actualItems.get(0).getAvailable(), equalTo(item1.getAvailable()));
        assertThat(actualItems.get(0).getOwner(), equalTo(item1.getOwner()));
        assertThat(actualItems.get(0).getRequest(), equalTo(item1.getRequest()));
    }

    @Test
    @DisplayName("'search' should return empty list by search criteria")
    public void returnEmptyListBySearchCriteria_Success() {
        User user1 = User.builder()
                .name("User 1")
                .email("user1Email@mail.ru")
                .build();

        User user2 = User.builder()
                .name("User 2")
                .email("user2Email@mail.ru")
                .build();

        em.persist(user1);
        em.persist(user2);

        ItemRequest itemRequest1 = ItemRequest.builder()
                .requestor(user2)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .requestor(user1)
                .build();

        em.persist(itemRequest1);
        em.persist(itemRequest2);

        Item item1 = Item.builder()
                .name("XBOX Series X")
                .description("Gaming console by Microsoft")
                .owner(user1)
                .available(true)
                .request(itemRequest1)
                .build();

        Item item2 = Item.builder()
                .name("PlayStation 5")
                .description("Gaming console by SONY")
                .owner(user2)
                .available(true)
                .request(itemRequest2)
                .build();

        em.persist(item1);
        em.persist(item2);

        List<Item> actualItems = itemRepository.search("console by Nintendo", OffsetPageRequest.of(0, 10));
        assertNotNull(actualItems);
        assertThat(actualItems.size(), equalTo(0));
    }
}
