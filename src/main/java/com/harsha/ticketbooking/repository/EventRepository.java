package com.harsha.ticketbooking.repository;

import com.harsha.ticketbooking.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event,Long> , JpaSpecificationExecutor<Event> {
    Page<Event> findByCategory(String category , Pageable pageable) ;
    Page<Event> findByVenueId(Long id , Pageable pageable) ;
    Page<Event> findByStartTimeAfter(LocalDateTime dateTime , Pageable pageable) ;
    Page<Event> findByCategoryAndVenueId(String category , Long id , Pageable pageable) ;

    @Query("SELECT e FROM Event e JOIN FETCH e.venue")
    List<Event> findAllWithVenue();

    @Query("SELECT e FROM Event e JOIN FETCH e.venue WHERE e.category = :category")
    List<Event> findByCategoryWithVenue(@Param("category") String category) ;

    @EntityGraph(attributePaths = {"venue"})
    @Query("SELECT e FROM Event e")
    Page<Event> findAllWithVenueGraph(Pageable pageable);

}
