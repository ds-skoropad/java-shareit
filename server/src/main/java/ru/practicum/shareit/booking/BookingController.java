package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @RequestBody BookingCreateDto bookingCreateDto
    ) {
        return bookingService.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @PathVariable Integer bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @PathVariable Integer bookingId
    ) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUserId(
            @RequestHeader(REQ_HEAD_USER_ID) Integer userId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState
    ) {
        return bookingService.getBookingsByUserId(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerId(
            @RequestHeader(REQ_HEAD_USER_ID) Integer ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") BookingState bookingState
    ) {
        return bookingService.getBookingsByOwnerId(ownerId, bookingState);
    }
}
