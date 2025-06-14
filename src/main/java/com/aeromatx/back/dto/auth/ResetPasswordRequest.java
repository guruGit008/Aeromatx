package com.aeromatx.back.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data; // Optional: If you're using Lombok for boilerplate code

// @Data annotation automatically generates getters, setters, toString, equals, and hashCode.
// If not using Lombok, you'll need to manually add getters and setters.
@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Reset token cannot be empty") // Ensures the token is present
    private String token;

    @NotBlank(message = "New password cannot be empty") // Ensures the new password is not null/empty
    // Example: enforce a minimum password length
    @Size(min = 4, message = "New password must be at least 8 characters long") 
    private String newPassword;

    // If not using Lombok's @Data, uncomment and use these:
    /*
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    */
}
