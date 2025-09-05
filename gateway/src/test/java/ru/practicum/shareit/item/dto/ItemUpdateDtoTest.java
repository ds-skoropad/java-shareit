package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemUpdateDtoTest {
    private static final String CORRECT_NAME = "Name";
    private static final String CORRECT_DESCRIPTION = "Description";
    private static final boolean CORRECT_AVAILABLE = true;
    private static final Integer CORRECT_REQUEST_ID = 0;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allCorrect() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(CORRECT_NAME, CORRECT_DESCRIPTION, CORRECT_AVAILABLE, CORRECT_REQUEST_ID);
        final Set<ConstraintViolation<ItemUpdateDto>> violations = validator.validate(itemUpdateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void allCorrectMayBeAllNull() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(null, null, null, null);
        final Set<ConstraintViolation<ItemUpdateDto>> violations = validator.validate(itemUpdateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void notCorrectNameShouldBeBlank() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(" ", CORRECT_DESCRIPTION, CORRECT_AVAILABLE, CORRECT_REQUEST_ID);
        final Set<ConstraintViolation<ItemUpdateDto>> violations = validator.validate(itemUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectDescriptionShouldBeBlank() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(CORRECT_NAME, " ", CORRECT_AVAILABLE, CORRECT_REQUEST_ID);
        final Set<ConstraintViolation<ItemUpdateDto>> violations = validator.validate(itemUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }
}