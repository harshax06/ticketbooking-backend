package com.harsha.ticketbooking.service;

import com.harsha.ticketbooking.entity.Event;
import com.harsha.ticketbooking.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EventService {
    private EventRepository eventRepository ;

    EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository ;
    }

    public Event create(Event event) {
        return eventRepository.save(event) ;
    }

    public List<Event> getAll() {
        return eventRepository.findAll() ;
    }

    public Event getById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Event not found with id : " + id)) ;
    }

    public Event update(Long id , Event updateEvent) {
        Event existing = getById(id) ;
        existing.setTitle(updateEvent.getTitle());
        existing.setCategory(updateEvent.getCategory());
        existing.setStartTime(updateEvent.getStartTime());
        existing.setVenue(updateEvent.getVenue());
        return eventRepository.save(existing) ;
    }

    public void delete(Long id) {
        Event existing = getById(id) ;
        eventRepository.delete(existing);
    }

}
