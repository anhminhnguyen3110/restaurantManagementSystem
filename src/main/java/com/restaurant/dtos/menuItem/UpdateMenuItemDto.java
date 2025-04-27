package com.restaurant.dtos.menuItem;

public class UpdateMenuItemDto extends CreateMenuItemDto {
    private int id;

    public UpdateMenuItemDto() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
