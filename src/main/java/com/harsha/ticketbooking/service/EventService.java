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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {

    private final EventRepository eventRepository ;
    private final VenueService venueService ;

    @PreAuthorize("hasRole('ORGANIZER') or hasRole('ADMIN')")
    @Transactional
    public EventResponseDto create(EventRequestDto dto) {
        Venue venue = venueService.findEntityById(dto.getVenueId());
        Event event = EventMapper.toEntity(dto,venue);
        Event saved = eventRepository.save(event);
        return EventMapper.toResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public EventResponseDto getById(Long id) {
        Event event = findEntityById(id);
        return EventMapper.toResponseDto(event);
    }

    @PreAuthorize("hasRole('ADMIN') or @eventSecurity.isOwner(#id, authentication.name)")
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


    @Cacheable(value = "events", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    @Transactional(readOnly = true)
    public List<EventResponseDto> getAllCached(Pageable pageable) {
        log.info("Cache miss - fetching events from database");
        return eventRepository.findAllWithVenueGraph(pageable)
                .map(EventMapper::toResponseDto)
                .getContent();
    }

    @Transactional(readOnly = true)
    public Page<EventResponseDto> getAll(Pageable pageable) {
        List<EventResponseDto> content = getAllCached(pageable);
        long total = eventRepository.count();  // total count, uncached but cheap
        return new org.springframework.data.domain.PageImpl<>(content, pageable, total);
    }

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<EventResponseDto> getDeletedEvents() {
        return eventRepository.findAllDeletedNative()
                .stream()
                .map(EventMapper::toResponseDto)
                .collect(Collectors.toList());
    }

}
