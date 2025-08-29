package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto createBooking(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @Valid @RequestBody BookingCreateDto bookingCreateDto
    ) {
        return bookingService.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto updateBookingStatus(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer bookingId,
            @RequestParam boolean approved
    ) {
        return bookingService.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBookingById(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer bookingId
    ) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingsByUserId(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsByUserId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingsByOwnerId(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer ownerId,
            @RequestParam(defaultValue = "ALL") String state
    ) {
        return bookingService.getBookingsByOwnerId(ownerId, state);
    }
}
