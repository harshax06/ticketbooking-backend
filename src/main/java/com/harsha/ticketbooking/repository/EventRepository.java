package com.harsha.ticketbooking.repository;

import com.harsha.ticketbooking.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event,Long> {
    Page<Event> findByCategory(String category , Pageable pageable) ;
    Page<Event> findByVenueId(Long id , Pageable pageable) ;
    Page<Event> findByStartTimeAfter(LocalDateTime dateTime , Pageable pageable) ;
    Page<Event> findByCategoryAndVenueId(String category , Long id , Pageable pageable) ;
}
