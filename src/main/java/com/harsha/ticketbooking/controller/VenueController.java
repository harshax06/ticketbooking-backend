package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.entity.Venue;
import com.harsha.ticketbooking.service.VenueService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/venues")
public class VenueController {
    private final VenueService venueService ;

    VenueController(VenueService venueService) {
        this.venueService = venueService ;
    }

    @PostMapping
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        Venue created = venueService.create(venue) ;
        URI location = URI.create("/api/v1/venues" + created.getId()) ;
        return ResponseEntity.created(location).body(created) ;
    }

    @GetMapping
    public ResponseEntity<List<Venue>> getAll() {
        return ResponseEntity.ok(venueService.getAll()) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getById(id)) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venue> update(@PathVariable Long id , @RequestBody Venue venue) {
        return ResponseEntity.ok(venueService.update(id,venue)) ;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build() ;
    }

}
