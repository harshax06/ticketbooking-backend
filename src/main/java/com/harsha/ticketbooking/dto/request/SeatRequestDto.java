package com.harsha.ticketbooking.dto.request;

import com.harsha.ticketbooking.entity.SeatType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SeatRequestDto {

    @NotBlank(message = "Row Label is required.")
    private String rowLabel ;

    @NotNull(message = "Seat Number is required.")
    private Integer seatNumber ;

    @NotNull(message = "Seat Type is required.")
    private SeatType seatType ;
}
