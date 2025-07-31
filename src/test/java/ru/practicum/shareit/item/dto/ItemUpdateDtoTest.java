package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemUpdateDtoTest {
    private static final Integer CORRECT_ID = null;
    private static final String CORRECT_NAME = "Name";
    private static final String CORRECT_DESCRIPTION = "Description";
    private static final boolean CORRECT_AVAILABLE = true;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allCorrect() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(CORRECT_ID, CORRECT_NAME, CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
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
    void notCorrectIdShouldBeNotNull() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(1, CORRECT_NAME, CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemUpdateDto>> violations = validator.validate(itemUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("id");
    }

    @Test
    void notCorrectNameShouldBeBlank() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(CORRECT_ID, " ", CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemUpdateDto>> violations = validator.validate(itemUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectDescriptionShouldBeBlank() {
        final ItemUpdateDto itemUpdateDto = new ItemUpdateDto(CORRECT_ID, CORRECT_NAME, " ", CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemUpdateDto>> violations = validator.validate(itemUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }
}