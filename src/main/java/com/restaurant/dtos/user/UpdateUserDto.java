package com.restaurant.dtos.user;

public class UpdateUserDto extends CreateUserDto {
    private int id;
    private Boolean active;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
