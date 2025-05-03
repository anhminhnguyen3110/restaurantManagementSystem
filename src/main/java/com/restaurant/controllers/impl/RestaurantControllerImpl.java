package com.restaurant.controllers.impl;

import com.restaurant.controllers.RestaurantController;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import com.restaurant.events.ErrorEvent;
import com.restaurant.models.Restaurant;
import com.restaurant.pubsub.ErrorPubSubService;
import com.restaurant.pubsub.PubSubService;

import java.util.List;

@Injectable
public class RestaurantControllerImpl implements RestaurantController {
    private final PubSubService pubSubService = ErrorPubSubService.getInstance();
    @Inject
    private RestaurantDAO restaurantDAO;

    public RestaurantControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createRestaurant(CreateRestaurantDto dto) {
        if (restaurantDAO.existsByNameAndAddress(dto.getName(), dto.getAddress())) {
            pubSubService.publish(new ErrorEvent("Duplicate restaurant: " + dto.getName()));
            return;
        }
        Restaurant r = new Restaurant();
        r.setName(dto.getName());
        r.setAddress(dto.getAddress());
        r.setMaxX(dto.getMaxX());
        r.setMaxY(dto.getMaxY());
        restaurantDAO.add(r);
    }

    @Override
    public void updateRestaurant(UpdateRestaurantDto dto) {
        Restaurant r = restaurantDAO.getById(dto.getId());
        if (r == null) {
            pubSubService.publish(new ErrorEvent("Restaurant not found: " + dto.getId()));
            return;
        }
        boolean nameChanged = !r.getName().equals(dto.getName());
        boolean addressChanged = !r.getAddress().equals(dto.getAddress());
        if ((nameChanged || addressChanged)
                && restaurantDAO.existsByNameAndAddress(dto.getName(), dto.getAddress())) {
            pubSubService.publish(new ErrorEvent("Duplicate restaurant: " + dto.getName()));
            return;
        }
        r.setName(dto.getName());
        r.setAddress(dto.getAddress());
        r.setStatus(dto.getStatus());
        restaurantDAO.update(r);
    }

    @Override
    public List<Restaurant> findRestaurants(GetRestaurantDto dto) {
        return restaurantDAO.find(dto);
    }

    @Override
    public Restaurant getRestaurantById(int id) {
        return restaurantDAO.getById(id);
    }

    @Override
    public List<Restaurant> findAllRestaurants() {
        return restaurantDAO.findAll();
    }
}
