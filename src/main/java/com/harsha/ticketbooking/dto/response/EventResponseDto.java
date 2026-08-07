package com.harsha.ticketbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {
    private Long id ;
    private String title ;
    private String category ;
    private LocalDateTime startTime ;
    private VenueResponseDto venue ;
}
