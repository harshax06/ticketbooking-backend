package com.harsha.ticketbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VenueResponseDto {
    private Long id ;
    private String name ;
    private String city ;
    private String address ;
    private Integer totalCapacity ;
}
