package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.entity.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(Long userId, Pageable pageable);

    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(Long userId, Pageable pageable);
}
