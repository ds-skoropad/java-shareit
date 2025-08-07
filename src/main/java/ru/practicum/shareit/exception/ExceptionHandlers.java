package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ExceptionResponse handleForbidden(final ForbiddenException e) {
        log.warn("Exception: Forbidden", e);
        return new ExceptionResponse("Forbidden", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.CONFLICT)
    public ExceptionResponse handleConflict(final ConflictException e) {
        log.warn("Exception: Conflict", e);
        return new ExceptionResponse("Conflict", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleNotValid(final ValidException e) {
        log.warn("Exception: Bad Request", e);
        return new ExceptionResponse("Bad Request", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleConstraintViolation(final ConstraintViolationException e) {
        log.warn("Exception: Bad Request", e);
        return new ExceptionResponse("Bad Request", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        StringBuilder sb = new StringBuilder("Validation failed: ");
        e.getBindingResult().getFieldErrors().forEach(f -> {
            sb.append(String.format("field = %s, message = %s", f.getField(), f.getDefaultMessage()));
        });
        log.warn("Exception: Bad Request", e);
        return new ExceptionResponse("Bad Request", sb.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handleNotFound(final NotFoundException e) {
        log.warn("Exception: Not Found", e);
        return new ExceptionResponse("Not Found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handleException(final Exception e) {
        log.warn("Exception: Internal Server Error", e);
        return new ExceptionResponse("Internal Server Error", "");
    }
}
