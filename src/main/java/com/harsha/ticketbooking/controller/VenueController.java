package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.dto.request.VenueRequestDto;
import com.harsha.ticketbooking.dto.response.VenueResponseDto;
import com.harsha.ticketbooking.entity.Venue;
import com.harsha.ticketbooking.service.VenueService;
import jakarta.validation.Valid;
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
    public ResponseEntity<VenueResponseDto> create(@Valid @RequestBody VenueRequestDto dto) {
        VenueResponseDto created = venueService.create(dto) ;
        URI location = URI.create("/api/v1/venues" + created.getId()) ;
        return ResponseEntity.created(location).body(created) ;
    }

    @GetMapping
    public ResponseEntity<List<VenueResponseDto>> getAll() {
        return ResponseEntity.ok(venueService.getAll()) ;
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getById(id)) ;
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponseDto> update(@PathVariable Long id , @Valid @RequestBody VenueRequestDto venue) {
        return ResponseEntity.ok(venueService.update(id,venue)) ;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build() ;
    }

}
