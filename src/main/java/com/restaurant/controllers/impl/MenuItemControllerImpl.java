package com.restaurant.controllers.impl;

import com.restaurant.controllers.MenuItemController;
import com.restaurant.daos.MenuDAO;
import com.restaurant.daos.MenuItemDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import com.restaurant.models.Menu;
import com.restaurant.models.MenuItem;

import java.util.List;

@Injectable
public class MenuItemControllerImpl implements MenuItemController {
    @Inject
    private MenuItemDAO menuItemDAO;

    @Inject
    private MenuDAO menuDAO;

    public MenuItemControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createMenuItem(CreateMenuItemDto dto) {
        if (menuItemDAO.existsByName(dto.getName())) {
            System.out.println("Duplicate menu item: " + dto.getName());
            return;
        }

        Menu menu = menuDAO.getById(dto.getMenuId());

        if (menu == null) {
            System.out.println("Menu not found: " + dto.getMenuId());
            return;
        }

        MenuItem item = new MenuItem();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        item.setMenu(menu);

        menuItemDAO.add(item);
    }

    @Override
    public void updateMenuItem(UpdateMenuItemDto dto) {
        MenuItem item = menuItemDAO.getById(dto.getId());
        if (item == null) return;
        if (!item.getName().equals(dto.getName())
                && menuItemDAO.existsByName(dto.getName(), dto.getId())) {
            System.out.println("Duplicate menu item: " + dto.getName());
            return;
        }
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        menuItemDAO.update(item);
    }

    @Override
    public List<MenuItem> findMenuItems(GetMenuItemsDto dto) {
        return menuItemDAO.find(dto);
    }

    @Override
    public void deleteMenuItem(int id) {
        menuItemDAO.delete(id);
    }

    @Override
    public MenuItem getMenuItem(int id) {
        return menuItemDAO.getById(id);
    }
}