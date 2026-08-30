package com.harsha.ticketbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthResponseDto {
    private String token ;
    private String email ;
    private String role ;
    private String refreshToken ;
}
