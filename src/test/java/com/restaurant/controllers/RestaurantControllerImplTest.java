package com.restaurant.controllers;

import com.restaurant.constants.RestaurantStatus;
import com.restaurant.controllers.impl.RestaurantControllerImpl;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.dtos.restaurant.CreateRestaurantDto;
import com.restaurant.dtos.restaurant.GetRestaurantDto;
import com.restaurant.dtos.restaurant.UpdateRestaurantDto;
import com.restaurant.models.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantControllerImplTest {
    @Mock
    RestaurantDAO restaurantDAO;
    @InjectMocks
    RestaurantControllerImpl controller;

    CreateRestaurantDto createDto;
    UpdateRestaurantDto updateDto;
    GetRestaurantDto getDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateRestaurantDto();
        updateDto = new UpdateRestaurantDto();
        getDto = new GetRestaurantDto();
    }

    @Test
    void createRestaurant_duplicate_noAdd() {
        createDto.setName("Shack");
        createDto.setAddress("123 Main");
        when(restaurantDAO.existsByNameAndAddress("Shack", "123 Main")).thenReturn(true);
        controller.createRestaurant(createDto);
        verify(restaurantDAO).existsByNameAndAddress("Shack", "123 Main");
        verifyNoMoreInteractions(restaurantDAO);
    }

    @Test
    void createRestaurant_success_addsRestaurant() {
        createDto.setName("Diner");
        createDto.setAddress("45 Elm");
        createDto.setMaxX(10);
        createDto.setMaxY(20);
        when(restaurantDAO.existsByNameAndAddress("Diner", "45 Elm")).thenReturn(false);
        controller.createRestaurant(createDto);
        ArgumentCaptor<Restaurant> capt = ArgumentCaptor.forClass(Restaurant.class);
        verify(restaurantDAO).add(capt.capture());
        Restaurant r = capt.getValue();
        assertEquals("Diner", r.getName());
        assertEquals("45 Elm", r.getAddress());
        assertEquals(10, r.getMaxX());
        assertEquals(20, r.getMaxY());
    }

    @Test
    void updateRestaurant_notFound_noUpdate() {
        updateDto.setId(5);
        when(restaurantDAO.getById(5)).thenReturn(null);
        controller.updateRestaurant(updateDto);
        verify(restaurantDAO).getById(5);
        verifyNoMoreInteractions(restaurantDAO);
    }

    @Test
    void updateRestaurant_duplicate_noUpdate() {
        Restaurant r = new Restaurant();
        r.setId(6);
        r.setName("Old");
        r.setAddress("Addr");
        when(restaurantDAO.getById(6)).thenReturn(r);
        updateDto.setId(6);
        updateDto.setName("New");
        updateDto.setAddress("NewAddr");
        when(restaurantDAO.existsByNameAndAddress("New", "NewAddr")).thenReturn(true);
        controller.updateRestaurant(updateDto);
        verify(restaurantDAO).getById(6);
        verify(restaurantDAO).existsByNameAndAddress("New", "NewAddr");
        verifyNoMoreInteractions(restaurantDAO);
    }

    @Test
    void updateRestaurant_success_updatesRestaurant() {
        Restaurant r = new Restaurant();
        r.setId(7);
        r.setName("A");
        r.setAddress("B");
        r.setStatus(RestaurantStatus.INACTIVE);
        when(restaurantDAO.getById(7)).thenReturn(r);
        updateDto.setId(7);
        updateDto.setName("C");
        updateDto.setAddress("D");
        updateDto.setStatus(RestaurantStatus.ACTIVE);
        when(restaurantDAO.existsByNameAndAddress("C", "D")).thenReturn(false);
        controller.updateRestaurant(updateDto);
        assertEquals("C", r.getName());
        assertEquals("D", r.getAddress());
        assertEquals(RestaurantStatus.ACTIVE, r.getStatus());
        verify(restaurantDAO).update(r);
    }

    @Test
    void findRestaurants_delegatesToDao() {
        List<Restaurant> list = List.of(new Restaurant(), new Restaurant());
        when(restaurantDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findRestaurants(getDto));
    }

    @Test
    void getRestaurantById_delegatesToDao() {
        Restaurant r = new Restaurant();
        when(restaurantDAO.getById(8)).thenReturn(r);
        assertSame(r, controller.getRestaurantById(8));
    }

    @Test
    void findAllRestaurants_delegatesToDao() {
        List<Restaurant> list = List.of(new Restaurant());
        when(restaurantDAO.findAll()).thenReturn(list);
        assertEquals(list, controller.findAllRestaurants());
    }
}
