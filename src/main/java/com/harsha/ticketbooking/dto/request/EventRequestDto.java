package com.harsha.ticketbooking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EventRequestDto {
    @NotBlank(message = "Event Title is required")
    private String title ;

    @NotBlank(message = "Category is required")
    private String category ;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime ;

    @NotNull(message = "Venue Id is required")
    private Long venueId ;
}
