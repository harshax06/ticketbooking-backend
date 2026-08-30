package com.harsha.ticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequestDto {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken ;
}
