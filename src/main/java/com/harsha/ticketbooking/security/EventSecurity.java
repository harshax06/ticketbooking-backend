package com.harsha.ticketbooking.security;

import com.harsha.ticketbooking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("eventSecurity")
@RequiredArgsConstructor
public class EventSecurity {

    private final EventRepository eventRepository ;

    public boolean isOwner(Long eventId , String email) {
        return eventRepository.findById(eventId)
                .map(e -> e.getOrganizer() != null && e.getOrganizer().getEmail().equals(email))
                .orElse(false) ;
    }
}
