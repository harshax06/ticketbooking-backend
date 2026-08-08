package com.harsha.ticketbooking.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private LocalDateTime timestamp ;
    private int status ;
    private String error ;
    private String message ;
    private String path ;
    private List<String> details ;
}
