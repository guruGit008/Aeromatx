// src/main/java/com/aeromatx/back/dto/user/UserDto.java
package com.aeromatx.back.dto.user;

import com.aeromatx.back.entity.User; // Assuming you have User entity

public class UserDto {
    private Long id;
    private String username;
    private String email;
    // Removed: private String phone;
    private String company; // Keep company if it's part of your User entity and you might use it elsewhere, but it won't be updated via this DTO anymore for phone-less scenario.

    // Constructor, getters, setters
    public UserDto() {}

    // Adjusted constructor
    public UserDto(Long id, String username, String email, String company) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.company = company;
    }

    public static UserDto fromEntity(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        // Removed: dto.setPhone(user.getPhone());
        dto.setCompany(user.getCompany()); // Map company from entity
        return dto;
    }

    // Getters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    // Removed: public String getPhone() { return phone; }
    public String getCompany() { return company; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    // Removed: public void setPhone(String phone) { this.phone = phone; }
    public void setCompany(String company) { this.company = company; }
}