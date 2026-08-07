package com.harsha.ticketbooking.mapper;

import com.harsha.ticketbooking.dto.request.EventRequestDto;
import com.harsha.ticketbooking.dto.response.EventResponseDto;
import com.harsha.ticketbooking.entity.Event;
import com.harsha.ticketbooking.entity.Venue;

public class EventMapper {

    public static Event toEntity(EventRequestDto dto , Venue venue) {
        Event event = new Event() ;
        event.setTitle(dto.getTitle());
        event.setCategory(dto.getCategory());
        event.setStartTime(dto.getStartTime());
        event.setVenue(venue);
        return event ;
    }

    public static EventResponseDto toResponseDto(Event event) {
        return new EventResponseDto(
                event.getId(),
                event.getTitle(),
                event.getCategory(),
                event.getStartTime(),
                VenueMapper.toResponseDto(event.getVenue())
        );
    }
}
