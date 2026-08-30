package com.harsha.ticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {

    @NotBlank(message = "Reset token is required")
    private String token;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;
}
