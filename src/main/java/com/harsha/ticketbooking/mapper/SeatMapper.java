package com.harsha.ticketbooking.mapper;

import com.harsha.ticketbooking.dto.request.SeatRequestDto;
import com.harsha.ticketbooking.dto.response.SeatResponseDto;
import com.harsha.ticketbooking.entity.Seat;
import com.harsha.ticketbooking.entity.Venue;

public class SeatMapper {
    public static Seat toEntity(SeatRequestDto dto , Venue venue) {
        Seat seat = new Seat() ;
        seat.setRowLabel(dto.getRowLabel());
        seat.setSeatNumber(dto.getSeatNumber());
        seat.setSeatType(dto.getSeatType());
        seat.setVenue(venue);
        return seat ;
    }

    public static SeatResponseDto toResponseDto(Seat seat) {
        return new SeatResponseDto(
                seat.getId(),
                seat.getRowLabel(),
                seat.getSeatNumber(),
                seat.getSeatType()
        );
    }
}
