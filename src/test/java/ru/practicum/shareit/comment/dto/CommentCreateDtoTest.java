package ru.practicum.shareit.comment.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.CommentConstants;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommentCreateDtoTest {
    private static final String CORRECT_TEXT = "Text";
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allCorrect() {
        final CommentCreateDto commentCreateDto = new CommentCreateDto(CORRECT_TEXT);
        final Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentCreateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void notCorrectTextShouldBeBlank() {
        final CommentCreateDto commentCreateDto = new CommentCreateDto(" ");
        final Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("text");
    }

    @Test
    void notCorrectTextShouldBeLossMinLength() {
        final String notCorrectText = "a".repeat(CommentConstants.TEXT_MIN_LENGTH - 1);
        final CommentCreateDto commentCreateDto = new CommentCreateDto(notCorrectText);
        final Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("text");
    }

    @Test
    void notCorrectTextShouldBeMoreMaxLength() {
        final String notCorrectText = "a".repeat(CommentConstants.TEXT_MAX_LENGTH + 1);
        final CommentCreateDto commentCreateDto = new CommentCreateDto(notCorrectText);
        final Set<ConstraintViolation<CommentCreateDto>> violations = validator.validate(commentCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("text");
    }
}