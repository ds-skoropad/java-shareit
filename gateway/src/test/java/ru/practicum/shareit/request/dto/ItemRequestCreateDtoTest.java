package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.ItemRequestConstants;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestCreateDtoTest {
    private static final String CORRECT_DESCRIPTION = "Description";
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allCorrect() {
        final ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(CORRECT_DESCRIPTION);
        final Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(itemRequestCreateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void notCorrectDescriptionShouldBeNull() {
        final ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(null);
        final Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(itemRequestCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }

    @Test
    void notCorrectDescriptionShouldBeBlank() {
        final ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(" ");
        final Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(itemRequestCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }

    @Test
    void notCorrectDescriptionShouldBeLossMinLength() {
        final String notCorrectDescription = "a".repeat(ItemRequestConstants.DESCRIPTION_MIN_LENGTH - 1);
        final ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(notCorrectDescription);
        final Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(itemRequestCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }

    @Test
    void notCorrectDescriptionShouldBeMoreMaxLength() {
        final String notCorrectDescription = "a".repeat(ItemRequestConstants.DESCRIPTION_MAX_LENGTH + 1);
        final ItemRequestCreateDto itemRequestCreateDto = new ItemRequestCreateDto(notCorrectDescription);
        final Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(itemRequestCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }
}