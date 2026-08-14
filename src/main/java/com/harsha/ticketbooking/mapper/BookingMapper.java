package com.harsha.ticketbooking.mapper;

import com.harsha.ticketbooking.dto.response.BookingResponseDto;
import com.harsha.ticketbooking.entity.Booking;

public class BookingMapper {
    public static BookingResponseDto toResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getEvent().getId(),
                SeatMapper.toResponseDto(booking.getSeat()),
                booking.getUser().getId(),
                booking.getStatus(),
                booking.getBookedAt()
        );
    }
}
