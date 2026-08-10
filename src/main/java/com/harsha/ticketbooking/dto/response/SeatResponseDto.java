package com.harsha.ticketbooking.dto.response;

import com.harsha.ticketbooking.entity.SeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDto {
    private Long id ;
    private String rowLabel ;
    private Integer seatNumber ;
    private SeatType seatType ;
}
