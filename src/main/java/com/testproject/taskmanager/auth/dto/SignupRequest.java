package com.testproject.taskmanager.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Between 3 to 50 character")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 50, message = "at least 4 character")
    private String password;
}
