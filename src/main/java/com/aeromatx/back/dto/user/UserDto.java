package com.aeromatx.back.dto.user; // Or your preferred DTO package

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

import com.aeromatx.back.entity.User;
// import com.aeromatx.back.entity.Role; // Assuming you have a Role entity

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String company;
    private Set<String> roles; // Return role names as strings

    // Static factory method to convert User entity to UserDto
    public static UserDto fromEntity(User user) {
        Set<String> roleNames = user.getRoles().stream()
                                    // Apply .name() to convert ERole enum to String
                                    .map(role -> role.getName().name()) 
                                    .collect(Collectors.toSet());
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCompany(),
            roleNames
        );
    }
}

