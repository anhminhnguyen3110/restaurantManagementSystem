package com.restaurant.controllers.impl;

import com.restaurant.controllers.MenuController;
import com.restaurant.daos.MenuDAO;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;
import com.restaurant.events.ErrorEvent;
import com.restaurant.models.Menu;
import com.restaurant.models.Restaurant;
import com.restaurant.pubsub.ErrorPubSubService;
import com.restaurant.pubsub.PubSubService;

import java.util.List;

@Injectable
public class MenuControllerImpl implements MenuController {
    private final PubSubService pubSubService = ErrorPubSubService.getInstance();
    @Inject
    private MenuDAO menuDAO;
    @Inject
    private RestaurantDAO restaurantDAO;

    public MenuControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createMenu(CreateMenuDto dto) {
        if (menuDAO.existsByNameAndRestaurant(dto.getName(), dto.getRestaurantId())) {
            pubSubService.publish(new ErrorEvent("Duplicate menu detected: name=" + dto.getName()));
            return;
        }
        Restaurant r = restaurantDAO.getById(dto.getRestaurantId());
        Menu m = new Menu();
        m.setName(dto.getName());
        m.setDescription(dto.getDescription());
        m.setRestaurant(r);
        menuDAO.add(m);
    }

    @Override
    public List<Menu> findMenus(GetMenuDto dto) {
        return menuDAO.find(dto);
    }

    @Override
    public void updateMenu(UpdateMenuDto dto) {
        Menu m = menuDAO.getById(dto.getId());
        if (m == null) return;
        if (!m.getName().equals(dto.getName())
                && menuDAO.existsByNameAndRestaurant(dto.getName(), dto.getRestaurantId(), dto.getId())) {
            pubSubService.publish(new ErrorEvent("Duplicate menu detected: name=" + dto.getName()));
            return;
        }
        m.setName(dto.getName());
        m.setDescription(dto.getDescription());
        if (m.getRestaurant().getId() != dto.getRestaurantId()) {
            Restaurant r = restaurantDAO.getById(dto.getRestaurantId());
            m.setRestaurant(r);
        }
        menuDAO.update(m);
    }

    @Override
    public void deleteMenu(int id) {
        menuDAO.delete(id);
    }

    @Override
    public Menu getMenu(int id) {
        return menuDAO.getById(id);
    }
}
