package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserConstants;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserUpdateDtoTest {
    private static final Integer CORRECT_ID = null;
    private static final String CORRECT_NAME = "Name";
    private static final String CORRECT_EMAIL = "email@domain.com";
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allCorrect() {
        final UserUpdateDto userUpdateDto = new UserUpdateDto(CORRECT_ID, CORRECT_NAME, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void allCorrectMayBeAllNull() {
        final UserUpdateDto userUpdateDto = new UserUpdateDto(null, null, null);
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void notCorrectIdShouldBeNotNull() {
        final UserUpdateDto userUpdateDto = new UserUpdateDto(1, CORRECT_NAME, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("id");
    }

    @Test
    void notCorrectNameShouldBeBlank() {
        final UserUpdateDto userUpdateDto = new UserUpdateDto(CORRECT_ID, " ", CORRECT_EMAIL);
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeLessMinLength() {
        final String NotCorrectName = "!".repeat(UserConstants.NAME_SIZE_MIN - 1);
        final UserUpdateDto userUpdateDto = new UserUpdateDto(CORRECT_ID, NotCorrectName, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeMoreMaxLength() {
        final String NotCorrectName = "!".repeat(UserConstants.NAME_SIZE_MAX + 1);
        final UserUpdateDto userUpdateDto = new UserUpdateDto(CORRECT_ID, NotCorrectName, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }


    @Test
    void notCorrectEmailShouldBeNotMatchTemplateEmail() {
        final UserUpdateDto userUpdateDto = new UserUpdateDto(CORRECT_ID, CORRECT_NAME, "email-domain.com");
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("email");
    }

    @Test
    void notCorrectEmailShouldBeMoreMaxLength() {
        final String notCorrectEmail = "a".repeat(UserConstants.EMAIL_SIZE_MAX).concat("@domain.com");
        final UserUpdateDto userUpdateDto = new UserUpdateDto(CORRECT_ID, CORRECT_NAME, notCorrectEmail);
        final Set<ConstraintViolation<UserUpdateDto>> violations = validator.validate(userUpdateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("email");
    }
}