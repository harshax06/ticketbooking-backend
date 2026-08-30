package com.harsha.ticketbooking.dto;

import com.harsha.ticketbooking.dto.request.EventRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.util.AssertionErrors.assertFalse;

public class EventRequestDtoValidationTest {

    private final Validator validator;

    EventRequestDtoValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void rejectsPastStartTime() {
        EventRequestDto dto = new EventRequestDto();
        dto.setTitle("Concert");
        dto.setCategory("Music");
        dto.setStartTime(LocalDateTime.now().minusDays(1)); // past date
        dto.setVenueId(1L);

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(dto);
        assertFalse("Past start time should fail validation", violations.isEmpty());
    }

    @Test
    void acceptsFutureStartTime() {
        EventRequestDto dto = new EventRequestDto();
        dto.setTitle("Concert");
        dto.setCategory("Music");
        dto.setStartTime(LocalDateTime.now().plusDays(1));
        dto.setVenueId(1L);

        Set<ConstraintViolation<EventRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }
}
