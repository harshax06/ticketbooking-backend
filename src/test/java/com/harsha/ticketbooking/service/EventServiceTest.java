package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.EventRequestDto;
import com.harsha.ticketbooking.entity.Event;
import com.harsha.ticketbooking.entity.Venue;
import com.harsha.ticketbooking.exception.ResourceNotFoundException;
import com.harsha.ticketbooking.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private VenueService venueService;

    @InjectMocks
    private EventService eventService;

    private EventRequestDto dto;
    private Venue venue;
    private Event event;

    @BeforeEach
    void setup() {
        venue = new Venue();
        venue.setId(1L);
        venue.setName("Test Venue");

        dto = new EventRequestDto();
        dto.setTitle("Concert");
        dto.setCategory("Music");
        dto.setStartTime(LocalDateTime.now().plusDays(5));
        dto.setVenueId(1L);

        event = new Event();
        event.setId(1L);
        event.setTitle("Concert");
        event.setCategory("Music");
        event.setStartTime(dto.getStartTime());
        event.setVenue(venue);
    }

    @Test
    void createsEventWhenVenueExists() {
        when(venueService.findEntityById(1L)).thenReturn(venue);
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = eventService.create(dto);

        assertEquals("Concert", result.getTitle());
        assertEquals("Music", result.getCategory());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void throwsWhenGettingNonExistentEvent() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.getById(99L));
    }

    @Test
    void returnsEventWhenFound() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        var result = eventService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Concert", result.getTitle());
    }

    @Test
    void updatesEventFieldsCorrectly() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(venueService.findEntityById(1L)).thenReturn(venue);
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        EventRequestDto updateDto = new EventRequestDto();
        updateDto.setTitle("Updated Concert");
        updateDto.setCategory("Live Music");
        updateDto.setStartTime(LocalDateTime.now().plusDays(10));
        updateDto.setVenueId(1L);

        var result = eventService.update(1L, updateDto);

        assertEquals("Updated Concert", result.getTitle());
        assertEquals("Live Music", result.getCategory());
    }

    @Test
    void deletesEventWhenExists() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        eventService.delete(1L);

        verify(eventRepository, times(1)).delete(event);
    }

    @Test
    void throwsWhenDeletingNonExistentEvent() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> eventService.delete(99L));
        verify(eventRepository, never()).delete((Event) any());
    }
}