package com.restaurant.dtos.user;

import com.restaurant.dtos.PaginationDto;

public class GetUserDto extends PaginationDto {
    private String username;
    private String email;
    private String role;

    public GetUserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
