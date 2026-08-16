package com.harsha.ticketbooking.controller;

import com.harsha.ticketbooking.dto.request.BookingRequestDto;
import com.harsha.ticketbooking.dto.response.BookingResponseDto;
import com.harsha.ticketbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService ;

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@Valid @RequestBody BookingRequestDto dto) {
        BookingResponseDto created = bookingService.createBooking(dto) ;
        URI location = URI.create("/api/v1/bookings/" + created.getId()) ;
        return ResponseEntity.created(location).body(created) ;
    }

    @PostMapping("/pessimistic")
    public ResponseEntity<BookingResponseDto> createPessimistic(@Valid @RequestBody BookingRequestDto dto) {
        BookingResponseDto created = bookingService.createBooking(dto) ;
        URI location = URI.create("/api/v1/bookings/" + created.getId()) ;
        return ResponseEntity.created(location).body(created) ;
    }
}
