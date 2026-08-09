package com.harsha.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"venue_id" , "row_label" , "seat_number"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "row_label" , nullable = false)
    private String rowLabel ;

    @Column(name = "seat_number" , nullable = false)
    private Integer seatNumber ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatType seatType ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id" , nullable = false)
    private Venue venue ;
}
