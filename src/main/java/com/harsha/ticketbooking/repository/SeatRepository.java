package com.harsha.ticketbooking.repository;

import com.harsha.ticketbooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,Long> {
    List<Seat> findByVenueId(Long venueId) ;
}
