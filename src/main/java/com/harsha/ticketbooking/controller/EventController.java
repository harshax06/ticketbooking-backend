package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.dto.request.EventRequestDto;
import com.harsha.ticketbooking.dto.response.EventResponseDto;
import com.harsha.ticketbooking.entity.Event;
import com.harsha.ticketbooking.service.EventService;
import jakarta.validation.Valid;
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
    public ResponseEntity<EventResponseDto> create(@Valid @RequestBody EventRequestDto dto) {
        EventResponseDto created = eventService.create(dto) ;
        URI location = URI.create("/api/v1/venues" + created.getId())  ;
        return ResponseEntity.created(location).body(created) ;
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAll() {
        return ResponseEntity.ok(eventService.getAll()) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id)) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDto> update(@PathVariable Long id ,@Valid @RequestBody EventRequestDto dto) {
        return ResponseEntity.ok(eventService.update(id,dto)) ;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build() ;
    }

}
