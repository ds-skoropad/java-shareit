package ru.practicum.shareit.user.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserConstants;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserCreateDtoTest {
    private static final String CORRECT_NAME = "Name";
    private static final String CORRECT_EMAIL = "email@domain.com";
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allCorrect() {
        final UserCreateDto userCreateDto = new UserCreateDto(CORRECT_NAME, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations.isEmpty()).isTrue();
    }

    @Test
    void notCorrectNameShouldBeNull() {
        final UserCreateDto userCreateDto = new UserCreateDto(null, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeBlank() {
        final UserCreateDto userCreateDto = new UserCreateDto(" ", CORRECT_EMAIL);
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeLessMinLength() {
        final String NotCorrectName = "!".repeat(UserConstants.NAME_SIZE_MIN - 1);
        final UserCreateDto userCreateDto = new UserCreateDto(NotCorrectName, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectNameShouldBeMoreMaxLength() {
        final String NotCorrectName = "!".repeat(UserConstants.NAME_SIZE_MAX + 1);
        final UserCreateDto userCreateDto = new UserCreateDto(NotCorrectName, CORRECT_EMAIL);
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("name");
    }

    @Test
    void notCorrectEmailShouldBeNull() {
        final UserCreateDto userCreateDto = new UserCreateDto(CORRECT_NAME, null);
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("email");

    }

    @Test
    void notCorrectEmailShouldBeNotMatchTemplateEmail() {
        final UserCreateDto userCreateDto = new UserCreateDto(CORRECT_NAME, "email-domain.com");
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("email");
    }

    @Test
    void notCorrectEmailShouldBeMoreMaxLength() {
        final String notCorrectEmail = "a".repeat(UserConstants.EMAIL_SIZE_MAX).concat("@domain.com");
        final UserCreateDto userCreateDto = new UserCreateDto(CORRECT_NAME, notCorrectEmail);
        final Set<ConstraintViolation<UserCreateDto>> violations = validator.validate(userCreateDto);

        assertThat(violations).extracting("propertyPath").extracting(Object::toString).containsOnly("email");
    }
}