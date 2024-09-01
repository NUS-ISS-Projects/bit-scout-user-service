package com.bitScout.userService.dto;

public class UserDTO {

    private String email;

    // Default constructor
    public UserDTO() {
    }

    // Parameterized constructor
    public UserDTO(String email, String displayName, String phoneNumber) {
        this.email = email;

    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "email='" + email + '\'' +
                +'}';
    }
}
