package com.harsha.ticketbooking.mapper;

import com.harsha.ticketbooking.dto.request.VenueRequestDto;
import com.harsha.ticketbooking.dto.response.VenueResponseDto;
import com.harsha.ticketbooking.entity.Venue;

public class VenueMapper {

    public static Venue toEntity(VenueRequestDto dto) {
        Venue venue = new Venue() ;
        venue.setName(dto.getName());
        venue.setCity(dto.getCity());
        venue.setAddress(dto.getAddress());
        venue.setTotalCapacity(dto.getTotalCapacity());
        return venue ;
    }

    public static VenueResponseDto toResponseDto(Venue venue) {
        return new VenueResponseDto(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getAddress(),
                venue.getTotalCapacity()
        );
    }
}
