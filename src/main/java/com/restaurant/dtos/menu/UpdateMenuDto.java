package com.restaurant.dtos.menu;

public class UpdateMenuDto extends CreateMenuDto {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
