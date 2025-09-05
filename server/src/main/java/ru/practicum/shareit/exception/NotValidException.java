package ru.practicum.shareit.exception;

public class NotValidException extends IllegalArgumentException {
    public NotValidException(String message) {
        super(message);
    }
}
