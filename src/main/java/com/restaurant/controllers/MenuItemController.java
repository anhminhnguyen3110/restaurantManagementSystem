package com.restaurant.controllers;

import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import com.restaurant.models.MenuItem;

public interface MenuItemController {
    void createMenuItem(CreateMenuItemDto createMenuItemDto);

    void updateMenuItem(UpdateMenuItemDto updateMenuItemDto);

    MenuItem findMenuItems(GetMenuItemsDto getMenuItemsDto);
}
