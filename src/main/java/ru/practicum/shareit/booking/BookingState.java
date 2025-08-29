package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.NotFoundException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState parseOf(String state) {
        try {
            return BookingState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException(String.format("Not found state: %s", state));
        }
    }
}
