package com.harsha.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private String name ;
    private String city ;
    private String address ;
    private Integer totalCapacity ;

    @OneToMany(mappedBy = "venue" , cascade = CascadeType.ALL , orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();
}
