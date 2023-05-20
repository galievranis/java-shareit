package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.entity.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(
            Long ownerId, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(
            Long ownerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartAfterOrderByStartDesc(
            Long ownerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(
            Long ownerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long ownerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<Booking> findAllByBookerIdOrderByStartDesc(
            Long bookerId, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(
            Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(
            Long bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(
            Long bookerId, LocalDateTime dateTime, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    Booking findFirstBookingByItemIdAndStartLessThanEqualAndStatusOrderByStartDesc(
            Long itemId, LocalDateTime dateTime, BookingStatus status);

    Booking findFirstBookingByItemIdAndStartAfterAndStatusOrderByStartAsc(
            Long itemId, LocalDateTime dateTime, BookingStatus status);

    List<Booking> findAllByItemIdInAndStatusOrderByStartAsc(
            List<Long> itemIds, BookingStatus status);

    Boolean existsByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, BookingStatus status, LocalDateTime dateTime);
}
