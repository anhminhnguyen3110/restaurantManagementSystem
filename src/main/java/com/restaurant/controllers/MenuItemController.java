package com.restaurant.controllers;

import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import com.restaurant.models.MenuItem;

import java.util.List;

public interface MenuItemController {
    void createMenuItem(CreateMenuItemDto createMenuItemDto);

    void updateMenuItem(UpdateMenuItemDto updateMenuItemDto);

    List<MenuItem> findMenuItems(GetMenuItemsDto getMenuItemsDto);

    void deleteMenuItem(int id);

    MenuItem getMenuItem(int id);
}
