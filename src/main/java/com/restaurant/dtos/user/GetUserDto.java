package com.restaurant.dtos.user;

import com.restaurant.constants.UserRole;
import com.restaurant.dtos.PaginationDto;

public class GetUserDto extends PaginationDto {
    private String username;
    private String email;
    private UserRole role;
    private String name;

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

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
