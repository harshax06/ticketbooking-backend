package com.harsha.ticketbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingRequestDto {

    @NotNull(message = "Event id is required.")
    private Long eventId ;

    @NotNull(message = "Seat id is required.")
    private Long seatId ;

    @NotNull(message = "User id is required.")
    private Long userId ;

}
