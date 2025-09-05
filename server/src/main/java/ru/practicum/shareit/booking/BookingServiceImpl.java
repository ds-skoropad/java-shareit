package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto createBooking(Integer userId, BookingCreateDto bookingCreateDto) {
        if (bookingCreateDto.start().isAfter(bookingCreateDto.end())) {
            throw new NotValidException("The start date must be before the end date.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Item item = itemRepository.findById(bookingCreateDto.itemId())
                .orElseThrow(() -> new NotFoundException(String.format("Item not found: id = %d",
                        bookingCreateDto.itemId())));
        if (!item.isAvailable()) {
            throw new NotValidException("Item not available for booking");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new NotValidException("Can't booking your item");
        }
        if (bookingRepository.existsByItemIdAndDateRangeAndStateIn(
                bookingCreateDto.itemId(),
                bookingCreateDto.start(),
                bookingCreateDto.end(),
                List.of(BookingStatus.APPROVED, BookingStatus.WAITING)
        )) {
            throw new NotValidException("Booking time in busy range");
        }


        Booking booking = BookingMapper.toBooking(bookingCreateDto, item, user);
        Booking createBooking = bookingRepository.save(booking);
        log.info("Create booking: {}", createBooking);

        return BookingMapper.toBookingResponseDto(createBooking);
    }

    @Override
    public BookingResponseDto updateBookingStatus(Integer userId, Integer bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking not found: id = %d", bookingId)));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new ForbiddenException("Only owners allowed");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new NotValidException("Booking has already been processed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking updateBooking = bookingRepository.save(booking);
        log.info("Update booking status: {}", updateBooking);

        return BookingMapper.toBookingResponseDto(updateBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Integer userId, Integer bookingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Booking not found: id = %d", bookingId)));
        if (userId.equals(booking.getBooker().getId()) &&
                userId.equals(booking.getItem().getOwner().getId())) {
            throw new ForbiddenException("Only owners allowed");
        }
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByUserId(Integer userId, BookingState bookingState) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", userId)));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
            case PAST -> bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponseDto> getBookingsByOwnerId(Integer ownerId, BookingState bookingState) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("User not found: id = %d", ownerId)));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (bookingState) {
            case ALL -> bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId);
            case CURRENT -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now,
                    now);
            case PAST -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId, now);
            case FUTURE -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(ownerId, now);
            case WAITING -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                    BookingStatus.WAITING);
            case REJECTED -> bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(ownerId,
                    BookingStatus.REJECTED);
        };

        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .toList();
    }
}
