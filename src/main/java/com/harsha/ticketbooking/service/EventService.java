package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.dto.request.EventRequestDto;
import com.harsha.ticketbooking.dto.response.EventResponseDto;
import com.harsha.ticketbooking.entity.Event;
import com.harsha.ticketbooking.entity.Venue;
import com.harsha.ticketbooking.exception.ResourceNotFoundException;
import com.harsha.ticketbooking.mapper.EventMapper;
import com.harsha.ticketbooking.repository.EventRepository;
import com.harsha.ticketbooking.repository.specification.EventSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository ;
    private final VenueService venueService ;

    @Transactional
    public EventResponseDto create(EventRequestDto dto) {
        Venue venue = venueService.findEntityById(dto.getVenueId());
        Event event = EventMapper.toEntity(dto,venue);
        Event saved = eventRepository.save(event);
        return EventMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponseDto> getAll() {
        return eventRepository.findAll()
                .stream()
                .map(EventMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EventResponseDto getById(Long id) {
        Event event = findEntityById(id);
        return EventMapper.toResponseDto(event);
    }

    @Transactional
    public EventResponseDto update(Long id , EventRequestDto dto) {
        Event existing = findEntityById(id) ;
        Venue venue = venueService.findEntityById(dto.getVenueId()) ;
        existing.setTitle(dto.getTitle());
        existing.setCategory(dto.getCategory());
        existing.setStartTime(dto.getStartTime());
        existing.setVenue(venue);
        Event updated = eventRepository.save(existing) ;
        return EventMapper.toResponseDto(updated) ;
    }

    @Transactional
    public void delete(Long id) {
        Event existing = findEntityById(id) ;
        eventRepository.delete(existing);
    }

    private Event findEntityById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id : "+id)) ;
    }

    public Page<EventResponseDto> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(EventMapper::toResponseDto);
    }

    public Page<EventResponseDto> getByCategory(String category , Pageable pageable) {
        return eventRepository.findByCategory(category,pageable)
                .map(EventMapper::toResponseDto);
    }

    public Page<EventResponseDto> getByVenueId(Long venueId , Pageable pageable) {
        return eventRepository.findByVenueId(venueId,pageable)
                .map(EventMapper::toResponseDto) ;
    }

    public Page<EventResponseDto> search(
            String city,
            String category,
            LocalDateTime from,
            LocalDateTime to,
            String keyword,
            Pageable pageable
    ) {
        Specification<Event> spec = Specification
                .where(EventSpecifications.hasCity(city))
                .and(EventSpecifications.hasCategory(category))
                .and(EventSpecifications.startsAfter(from))
                .and(EventSpecifications.startsBefore(to))
                .and(EventSpecifications.titleContains(keyword)) ;

        return eventRepository.findAll(spec , pageable)
                .map(EventMapper::toResponseDto) ;
    }

    public Page<EventResponseDto> findAll(Pageable pageable) {
        return eventRepository.findAllWithVenueGraph(pageable)
                .map(EventMapper::toResponseDto) ;
    }

}
