package com.harsha.ticketbooking.repository;

import com.harsha.ticketbooking.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SeatRepository extends JpaRepository<Seat,Long> {
    Page<Seat> findByVenueId(Long venueId , Pageable pageable) ;
}
