package com.harsha.ticketbooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

    @NotBlank(message = "Name is required")
    private String name ;

    @Email(message = "A valid email is required")
    @NotBlank(message = "Email is required")
    private String email ;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password ;
}
