package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemConstants;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCreateDtoTest {
    private static final String CORRECT_NAME = "Name";
    private static final String CORRECT_DESCRIPTION = "Description";
    private static final boolean CORRECT_AVAILABLE = true;
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allCorrect() {
        final ItemCreateDto itemCreateDto = new ItemCreateDto(CORRECT_NAME, CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void notCorrectNameShouldBeNull() {
        final ItemCreateDto itemCreateDto = new ItemCreateDto(null, CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeBlank() {
        final ItemCreateDto itemCreateDto = new ItemCreateDto(" ", CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeLossMinLength() {
        final String notCorrectName = "a".repeat(ItemConstants.NAME_MIN_LENGTH - 1);
        final ItemCreateDto itemCreateDto = new ItemCreateDto(notCorrectName, CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeMoreMaxLength() {
        final String notCorrectName = "a".repeat(ItemConstants.NAME_MAX_LENGTH + 1);
        final ItemCreateDto itemCreateDto = new ItemCreateDto(notCorrectName, CORRECT_DESCRIPTION, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectDescriptionShouldBeNull() {
        final ItemCreateDto itemCreateDto = new ItemCreateDto(CORRECT_NAME, null, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }

    @Test
    void notCorrectDescriptionShouldBeBlank() {
        final ItemCreateDto itemCreateDto = new ItemCreateDto(CORRECT_NAME, " ", CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }

    @Test
    void notCorrectDescriptionShouldBeLossMinLength() {
        final String notCorrectDescription = "a".repeat(ItemConstants.DESCRIPTION_MIN_LENGTH - 1);
        final ItemCreateDto itemCreateDto = new ItemCreateDto(CORRECT_NAME, notCorrectDescription, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }

    @Test
    void notCorrectDescriptionShouldBeMoreMaxLength() {
        final String notCorrectDescription = "a".repeat(ItemConstants.DESCRIPTION_MAX_LENGTH + 1);
        final ItemCreateDto itemCreateDto = new ItemCreateDto(CORRECT_NAME, notCorrectDescription, CORRECT_AVAILABLE);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("description");
    }

    @Test
    void notCorrectAvailableShouldBeNull() {
        final ItemCreateDto itemCreateDto = new ItemCreateDto(CORRECT_NAME, CORRECT_DESCRIPTION, null);
        final Set<ConstraintViolation<ItemCreateDto>> violations = validator.validate(itemCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("available");
    }
}