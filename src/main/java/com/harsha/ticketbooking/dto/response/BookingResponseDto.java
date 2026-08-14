package com.harsha.ticketbooking.dto.response;

import com.harsha.ticketbooking.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDto {
    private Long id ;
    private Long eventId ;
    private SeatResponseDto seat ;
    private Long userId ;
    private BookingStatus bookingStatus ;
    private LocalDateTime bookedAt ;
}
