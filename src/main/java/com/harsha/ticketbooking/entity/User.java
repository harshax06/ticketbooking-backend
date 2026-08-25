package com.harsha.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(nullable = false , unique = true)
    private String email ;

    @Column(nullable = false)
    private String name ;

    @Column(nullable = false)
    private String passwordHash ;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER ;
}
