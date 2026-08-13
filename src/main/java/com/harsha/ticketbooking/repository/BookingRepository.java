package com.harsha.ticketbooking.repository;

import com.harsha.ticketbooking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    Page<Booking> findByUserId(Long userId , Pageable pageable) ;
    Page<Booking> findByEventId(Long eventId , Pageable pageable) ;
}
