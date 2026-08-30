package com.harsha.ticketbooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequestDto {

    @Email(message = "A valid email is required")
    @NotBlank(message = "Email is required")
    private String email ;
}
