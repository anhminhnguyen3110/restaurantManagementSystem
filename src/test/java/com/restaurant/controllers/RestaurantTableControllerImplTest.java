package com.restaurant.controllers;

import com.restaurant.controllers.impl.RestaurantTableControllerImpl;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.daos.RestaurantTableDAO;
import com.restaurant.dtos.restaurantTable.CreateRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableDto;
import com.restaurant.dtos.restaurantTable.GetRestaurantTableForBookingDto;
import com.restaurant.dtos.restaurantTable.UpdateRestaurantTableDto;
import com.restaurant.models.Restaurant;
import com.restaurant.models.RestaurantTable;
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
class RestaurantTableControllerImplTest {
    @Mock
    RestaurantTableDAO tableDAO;
    @Mock
    RestaurantDAO restaurantDAO;
    @InjectMocks
    RestaurantTableControllerImpl controller;

    CreateRestaurantTableDto createDto;
    UpdateRestaurantTableDto updateDto;
    GetRestaurantTableDto getDto;
    GetRestaurantTableForBookingDto bookingDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateRestaurantTableDto();
        updateDto = new UpdateRestaurantTableDto();
        getDto = new GetRestaurantTableDto();
        bookingDto = new GetRestaurantTableForBookingDto();
    }

    @Test
    void createTable_startPositionTaken_noAdd() {
        createDto.setRestaurantId(1);
        createDto.setStartX(5);
        createDto.setStartY(6);
        when(tableDAO.existsByRestaurantIdAndStartPosition(1, 5, 6, null)).thenReturn(true);
        controller.createTable(createDto);
        verify(tableDAO).existsByRestaurantIdAndStartPosition(1, 5, 6, null);
        verifyNoMoreInteractions(tableDAO, restaurantDAO);
    }

    @Test
    void createTable_endPositionTaken_noAdd() {
        createDto.setRestaurantId(2);
        createDto.setStartX(0);
        createDto.setStartY(0);
        createDto.setEndX(7);
        createDto.setEndY(8);
        when(tableDAO.existsByRestaurantIdAndStartPosition(2, 0, 0, null)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndEndPosition(2, 7, 8, null)).thenReturn(true);
        controller.createTable(createDto);
        verify(tableDAO).existsByRestaurantIdAndStartPosition(2, 0, 0, null);
        verify(tableDAO).existsByRestaurantIdAndEndPosition(2, 7, 8, null);
        verifyNoMoreInteractions(tableDAO, restaurantDAO);
    }

    @Test
    void createTable_numberTaken_noAdd() {
        createDto.setRestaurantId(3);
        createDto.setStartX(0);
        createDto.setStartY(0);
        createDto.setEndX(0);
        createDto.setEndY(0);
        createDto.setNumber(4);
        when(tableDAO.existsByRestaurantIdAndStartPosition(3, 0, 0, null)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndEndPosition(3, 0, 0, null)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndNumber(3, 4, null)).thenReturn(true);
        controller.createTable(createDto);
        verify(tableDAO).existsByRestaurantIdAndStartPosition(3, 0, 0, null);
        verify(tableDAO).existsByRestaurantIdAndEndPosition(3, 0, 0, null);
        verify(tableDAO).existsByRestaurantIdAndNumber(3, 4, null);
        verifyNoMoreInteractions(tableDAO, restaurantDAO);
    }

    @Test
    void createTable_success_addsTable() {
        createDto.setRestaurantId(5);
        createDto.setStartX(1);
        createDto.setStartY(2);
        createDto.setEndX(3);
        createDto.setEndY(4);
        createDto.setNumber(10);
        createDto.setCapacity(20);
        when(tableDAO.existsByRestaurantIdAndStartPosition(5, 1, 2, null)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndEndPosition(5, 3, 4, null)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndNumber(5, 10, null)).thenReturn(false);
        Restaurant r = new Restaurant();
        r.setId(5);
        when(restaurantDAO.getById(5)).thenReturn(r);

        controller.createTable(createDto);

        ArgumentCaptor<RestaurantTable> capt = ArgumentCaptor.forClass(RestaurantTable.class);
        verify(tableDAO).add(capt.capture());
        RestaurantTable t = capt.getValue();
        assertSame(r, t.getRestaurant());
        assertEquals(10, t.getNumber());
        assertEquals(20, t.getCapacity());
        assertEquals(1, t.getStartX());
        assertEquals(2, t.getStartY());
    }

    @Test
    void updateTable_notFound_noUpdate() {
        updateDto.setId(7);
        when(tableDAO.getById(7)).thenReturn(null);
        controller.updateTable(updateDto);
        verify(tableDAO).getById(7);
        verifyNoMoreInteractions(tableDAO, restaurantDAO);
    }

    @Test
    void updateTable_startPositionTaken_noUpdate() {
        updateDto.setId(8);
        updateDto.setRestaurantId(9);
        updateDto.setStartX(1);
        updateDto.setStartY(2);
        when(tableDAO.getById(8)).thenReturn(new RestaurantTable());
        when(tableDAO.existsByRestaurantIdAndStartPosition(9, 1, 2, 8)).thenReturn(true);
        controller.updateTable(updateDto);
        verify(tableDAO).existsByRestaurantIdAndStartPosition(9, 1, 2, 8);
        verifyNoMoreInteractions(tableDAO, restaurantDAO);
    }

    @Test
    void updateTable_endPositionTaken_noUpdate() {
        updateDto.setId(10);
        updateDto.setRestaurantId(11);
        updateDto.setStartX(0);
        updateDto.setStartY(0);
        updateDto.setEndX(3);
        updateDto.setEndY(4);
        when(tableDAO.getById(10)).thenReturn(new RestaurantTable());
        when(tableDAO.existsByRestaurantIdAndStartPosition(11, 0, 0, 10)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndEndPosition(11, 3, 4, 10)).thenReturn(true);
        controller.updateTable(updateDto);
        verify(tableDAO).existsByRestaurantIdAndEndPosition(11, 3, 4, 10);
        verifyNoMoreInteractions(tableDAO, restaurantDAO);
    }

    @Test
    void updateTable_numberTaken_noUpdate() {
        updateDto.setId(12);
        updateDto.setRestaurantId(13);
        updateDto.setStartX(0);
        updateDto.setStartY(0);
        updateDto.setEndX(0);
        updateDto.setEndY(0);
        updateDto.setNumber(5);
        when(tableDAO.getById(12)).thenReturn(new RestaurantTable());
        when(tableDAO.existsByRestaurantIdAndStartPosition(13, 0, 0, 12)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndEndPosition(13, 0, 0, 12)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndNumber(13, 5, 12)).thenReturn(true);
        controller.updateTable(updateDto);
        verify(tableDAO).existsByRestaurantIdAndNumber(13, 5, 12);
        verifyNoMoreInteractions(tableDAO, restaurantDAO);
    }

    @Test
    void updateTable_success_updatesTable() {
        updateDto.setId(14);
        updateDto.setRestaurantId(15);
        updateDto.setStartX(1);
        updateDto.setStartY(2);
        updateDto.setEndX(3);
        updateDto.setEndY(4);
        updateDto.setNumber(6);
        updateDto.setCapacity(8);
        RestaurantTable t = new RestaurantTable();
        when(tableDAO.getById(14)).thenReturn(t);
        when(tableDAO.existsByRestaurantIdAndStartPosition(15, 1, 2, 14)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndEndPosition(15, 3, 4, 14)).thenReturn(false);
        when(tableDAO.existsByRestaurantIdAndNumber(15, 6, 14)).thenReturn(false);
        Restaurant r = new Restaurant();
        r.setId(15);
        when(restaurantDAO.getById(15)).thenReturn(r);

        controller.updateTable(updateDto);

        assertSame(r, t.getRestaurant());
        assertEquals(6, t.getNumber());
        assertEquals(8, t.getCapacity());
        assertEquals(1, t.getStartX());
        assertEquals(2, t.getStartY());
        assertEquals(3, t.getEndX());
        assertEquals(4, t.getEndY());
        verify(tableDAO).update(t);
    }

    @Test
    void findTables_delegatesToDao() {
        List<RestaurantTable> list = List.of(new RestaurantTable(), new RestaurantTable());
        when(tableDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findTables(getDto));
    }

    @Test
    void findTablesForBooking_delegatesToDao() {
        List<RestaurantTable> list = List.of(new RestaurantTable());
        when(tableDAO.findForBooking(bookingDto)).thenReturn(list);
        assertEquals(list, controller.findTablesForBooking(bookingDto));
    }

    @Test
    void findAllTablesForOrder_delegatesToDao() {
        List<RestaurantTable> list = List.of(new RestaurantTable());
        when(tableDAO.findTablesForOrder(20)).thenReturn(list);
        assertEquals(list, controller.findAllTablesForOrder(20));
    }
}
