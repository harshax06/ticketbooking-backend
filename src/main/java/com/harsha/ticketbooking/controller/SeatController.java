package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.dto.request.SeatRequestDto;
import com.harsha.ticketbooking.dto.response.SeatResponseDto;
import com.harsha.ticketbooking.service.SeatService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/venues/{venueId}/seats")
public class SeatController {
    private final SeatService seatService ;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<List<SeatResponseDto>> createBulk(@PathVariable Long venueId , @Valid @RequestBody List<SeatRequestDto> seatDtos) {
        return ResponseEntity.ok(seatService.createBulk(venueId,seatDtos)) ;
    }

    @GetMapping
    public ResponseEntity<Page<SeatResponseDto>> getByVenue(@PathVariable("venueId") Long id , Pageable pageable) {
        return ResponseEntity.ok(seatService.getByVenue(id,pageable)) ;
    }
}
