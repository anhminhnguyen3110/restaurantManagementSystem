package com.restaurant.controllers;

import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;
import com.restaurant.models.Menu;

import java.util.List;

public interface MenuController {
    void createMenu(CreateMenuDto createMenuDto);

    void updateMenu(UpdateMenuDto updateMenuDto);

    void deleteMenu(int id);

    List<Menu> findMenus(GetMenuDto getMenuDto);
    
    Menu getMenu(int id);
}
