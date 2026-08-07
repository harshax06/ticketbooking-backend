package com.harsha.ticketbooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VenueRequestDto {
    @NotBlank(message = "Venue name is required")
    private String name ;

    @NotBlank(message = "City is required")
    private String city ;

    @NotBlank(message = "Address is required")
    private String address ;

    @Min(value = 1 , message = "Total capacity is must be at least 1")
    private Integer totalCapacity ;
}
