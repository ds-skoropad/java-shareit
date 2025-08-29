package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBookerIdOrderByStartDesc(Integer bookerId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer bookerId, LocalDateTime start,
                                                                          LocalDateTime end);

    List<Booking> findByBookerIdAndEndBeforeOrderByStartDesc(Integer bookerId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfterOrderByStartDesc(Integer bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Integer bookerId, BookingStatus bookingStatus);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Integer ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer ownerId, LocalDateTime start,
                                                                             LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStartDesc(Integer ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Integer ownerId, BookingStatus bookingStatus);

    boolean existsByBookerIdAndItemIdAndEndBefore(Integer bookerId, Integer itemId, LocalDateTime end);

    Optional<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(Integer itemId, LocalDateTime now);

    Optional<Booking> findFirstByItemIdAndStartAfterOrderByStartAsc(Integer itemId, LocalDateTime now);

    List<Booking> findByItemIdInAndStatus(List<Integer> itemIds, BookingStatus status);
}
