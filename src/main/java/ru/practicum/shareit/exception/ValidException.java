package ru.practicum.shareit.exception;

public class ValidException extends IllegalArgumentException {
    public ValidException(String message) {
        super(message);
    }
}
