package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.entity.Event;
import com.harsha.ticketbooking.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {
    private final EventService eventService ;

    EventController(EventService eventService) {
        this.eventService = eventService ;
    }

    @PostMapping
    public ResponseEntity<Event> create(@RequestBody Event event) {
        Event created = eventService.create(event) ;
        URI location = URI.create("/api/v1/venues" + created.getId())  ;
        return ResponseEntity.created(location).body(created) ;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        return ResponseEntity.ok(eventService.getAll()) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id)) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> update(@PathVariable Long id , @RequestBody Event event) {
        return ResponseEntity.ok(eventService.update(id,event)) ;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build() ;
    }

}
