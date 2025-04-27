package com.restaurant.controllers.impl;

import com.restaurant.controllers.RestaurantTableController;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;

import java.util.List;

@Injectable
public class RestaurantTableControllerImpl implements RestaurantTableController {
    @Inject
    private RestaurantTableDAO restaurantTableDAO;
    @Inject
    private RestaurantDAO restaurantDAO;

    public RestaurantTableControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createTable(CreateRestaurantTableDto dto) {
        if (restaurantTableDAO.existsByRestaurantIdAndStartPosition(
                dto.getRestaurantId(), dto.getStartX(), dto.getStartY(), null)) {
            System.out.println("Position (" + dto.getStartX() + "," + dto.getStartY() + ") is already taken in restaurant " + dto.getRestaurantId());
            return;
        }

        if (
                restaurantTableDAO.existsByRestaurantIdAndEndPosition(
                        dto.getRestaurantId(), dto.getEndX(), dto.getEndY(), null)) {
            System.out.println("Position (" + dto.getEndX() + "," + dto.getEndY() + ") is already taken in restaurant " + dto.getRestaurantId());
            return;
        }

        if (restaurantTableDAO.existsByRestaurantIdAndNumber(
                dto.getRestaurantId(), dto.getNumber(), null)) {
            System.out.println("Table number " + dto.getNumber() + " already exists in restaurant " + dto.getRestaurantId());
            return;
        }

        Restaurant r = restaurantDAO.getById(dto.getRestaurantId());
        RestaurantTable t = new RestaurantTable();
        t.setRestaurant(r);
        t.setNumber(dto.getNumber());
        t.setCapacity(dto.getCapacity());
        t.setStartX(dto.getStartX());
        t.setStartY(dto.getStartY());
        restaurantTableDAO.add(t);
    }

    @Override
    public void updateTable(UpdateRestaurantTableDto dto) {
        RestaurantTable t = restaurantTableDAO.getById(dto.getId());
        if (t == null) return;

        if (restaurantTableDAO.existsByRestaurantIdAndStartPosition(
                dto.getRestaurantId(), dto.getStartX(), dto.getStartY(), dto.getId())) {
            System.out.println("Position (" + dto.getStartX() + "," + dto.getStartY() + ") is already taken in restaurant " + dto.getRestaurantId());
            return;
        }

        if (restaurantTableDAO.existsByRestaurantIdAndEndPosition(
                dto.getRestaurantId(), dto.getEndX(), dto.getEndY(), dto.getId())) {
            System.out.println("Position (" + dto.getEndX() + "," + dto.getEndY() + ") is already taken in restaurant " + dto.getRestaurantId());
            return;
        }

        if (restaurantTableDAO.existsByRestaurantIdAndNumber(
                dto.getRestaurantId(), dto.getNumber(), dto.getId())) {
            System.out.println("Table number " + dto.getNumber() + " already exists in restaurant " + dto.getRestaurantId());
            return;
        }
        Restaurant r = restaurantDAO.getById(dto.getRestaurantId());
        t.setRestaurant(r);
        t.setNumber(dto.getNumber());
        t.setCapacity(dto.getCapacity());

        t.setStartX(dto.getStartX());
        t.setStartY(dto.getStartY());
        t.setEndX(dto.getEndX());
        t.setEndY(dto.getEndY());

        restaurantTableDAO.update(t);
    }

    @Override
    public void deleteTable(int id) {
        restaurantTableDAO.delete(id);
    }

    @Override
    public List<RestaurantTable> findTables(GetRestaurantTableDto dto) {
        return restaurantTableDAO.find(dto);
    }

    @Override
    public List<RestaurantTable> findTablesForBooking(GetRestaurantTableForBookingDto dto) {
        return restaurantTableDAO.findForBooking(dto);
    }

    @Override
    public RestaurantTable getTable(int id) {
        return restaurantTableDAO.getById(id);
    }
}