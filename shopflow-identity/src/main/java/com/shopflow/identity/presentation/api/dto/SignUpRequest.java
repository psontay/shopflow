package com.shopflow.identity.presentation.api.dto;

import com.shopflow.identity.application.commands.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignUpRequest(

        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3,
                max = 20,
                message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        @Size(max = 50)
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6,
                max = 40,
                message = "Password must be between 6 and 40 characters")
        String password,

        @NotBlank(message = "Confirm password doesn't match")
        String confirmPassword

) {

    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(username, email, password, confirmPassword);
    }

}
