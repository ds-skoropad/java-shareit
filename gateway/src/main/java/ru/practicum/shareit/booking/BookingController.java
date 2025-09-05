package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingState;

import static ru.practicum.shareit.ShareItConstants.REQ_HEAD_USER_ID;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @Valid @RequestBody BookingCreateDto bookingCreateDto) {
        return bookingClient.createBooking(userId, bookingCreateDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer bookingId,
            @RequestParam boolean approved) {
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @PathVariable @Min(1) Integer bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUserId(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer userId,
            @RequestParam(defaultValue = "ALL") BookingState bookingState) {
        return bookingClient.getBookingsByUserId(userId, bookingState);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(
            @RequestHeader(REQ_HEAD_USER_ID) @Min(1) Integer ownerId,
            @RequestParam(defaultValue = "ALL") BookingState bookingState) {
        return bookingClient.getBookingsByOwnerId(ownerId, bookingState);
    }
}
