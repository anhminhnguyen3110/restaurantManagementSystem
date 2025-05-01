package com.restaurant.controllers;

import com.restaurant.controllers.impl.MenuControllerImpl;
import com.restaurant.daos.MenuDAO;
import com.restaurant.daos.RestaurantDAO;
import com.restaurant.dtos.menu.CreateMenuDto;
import com.restaurant.dtos.menu.GetMenuDto;
import com.restaurant.dtos.menu.UpdateMenuDto;
import com.restaurant.models.Menu;
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
class MenuControllerImplTest {
    @Mock
    MenuDAO menuDAO;
    @Mock
    RestaurantDAO restaurantDAO;
    @InjectMocks
    MenuControllerImpl controller;

    CreateMenuDto createDto;
    GetMenuDto getDto;
    UpdateMenuDto updateDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateMenuDto();
        getDto = new GetMenuDto();
        updateDto = new UpdateMenuDto();
    }

    @Test
    void createMenu_duplicate_noAdd() {
        createDto.setName("Breakfast");
        createDto.setRestaurantId(5);
        when(menuDAO.existsByNameAndRestaurant("Breakfast", 5)).thenReturn(true);
        controller.createMenu(createDto);
        verify(menuDAO).existsByNameAndRestaurant("Breakfast", 5);
        verifyNoMoreInteractions(menuDAO, restaurantDAO);
    }

    @Test
    void createMenu_newMenu_addsMenu() {
        createDto.setName("Lunch");
        createDto.setDescription("Tasty");
        createDto.setRestaurantId(3);
        when(menuDAO.existsByNameAndRestaurant("Lunch", 3)).thenReturn(false);
        Restaurant r = new Restaurant();
        r.setId(3);
        when(restaurantDAO.getById(3)).thenReturn(r);
        controller.createMenu(createDto);
        ArgumentCaptor<Menu> capt = ArgumentCaptor.forClass(Menu.class);
        verify(menuDAO).add(capt.capture());
        Menu m = capt.getValue();
        assertEquals("Lunch", m.getName());
        assertEquals("Tasty", m.getDescription());
        assertSame(r, m.getRestaurant());
    }

    @Test
    void findMenus_delegatesToDao() {
        List<Menu> list = List.of(new Menu(), new Menu());
        when(menuDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findMenus(getDto));
    }

    @Test
    void updateMenu_notFound_noUpdate() {
        updateDto.setId(7);
        when(menuDAO.getById(7)).thenReturn(null);
        controller.updateMenu(updateDto);
        verify(menuDAO).getById(7);
        verifyNoMoreInteractions(menuDAO, restaurantDAO);
    }

    @Test
    void updateMenu_duplicateName_noUpdate() {
        Menu m = new Menu();
        m.setId(8);
        m.setName("Old");
        m.setRestaurant(new Restaurant() {
            {
                setId(2);
            }
        });
        when(menuDAO.getById(8)).thenReturn(m);
        updateDto.setId(8);
        updateDto.setName("New");
        updateDto.setRestaurantId(2);
        when(menuDAO.existsByNameAndRestaurant("New", 2, 8)).thenReturn(true);
        controller.updateMenu(updateDto);
        verify(menuDAO).existsByNameAndRestaurant("New", 2, 8);
        verify(menuDAO, never()).update(any());
    }

    @Test
    void updateMenu_nameAndRestaurantChange_updates() {
        Restaurant oldR = new Restaurant();
        oldR.setId(1);
        Restaurant newR = new Restaurant();
        newR.setId(4);
        Menu m = new Menu();
        m.setId(9);
        m.setName("A");
        m.setDescription("D");
        m.setRestaurant(oldR);
        when(menuDAO.getById(9)).thenReturn(m);
        updateDto.setId(9);
        updateDto.setName("B");
        updateDto.setDescription("E");
        updateDto.setRestaurantId(4);
        when(menuDAO.existsByNameAndRestaurant("B", 4, 9)).thenReturn(false);
        when(restaurantDAO.getById(4)).thenReturn(newR);
        controller.updateMenu(updateDto);
        assertEquals("B", m.getName());
        assertEquals("E", m.getDescription());
        assertSame(newR, m.getRestaurant());
        verify(menuDAO).update(m);
    }

    @Test
    void updateMenu_nameSameRestaurant_updatesNameAndDesc() {
        Restaurant r = new Restaurant();
        r.setId(6);
        Menu m = new Menu();
        m.setId(10);
        m.setName("X");
        m.setDescription("Y");
        m.setRestaurant(r);
        when(menuDAO.getById(10)).thenReturn(m);
        updateDto.setId(10);
        updateDto.setName("X");
        updateDto.setDescription("Z");
        updateDto.setRestaurantId(6);
        controller.updateMenu(updateDto);
        assertEquals("X", m.getName());
        assertEquals("Z", m.getDescription());
        assertSame(r, m.getRestaurant());
        verify(menuDAO).update(m);
    }

    @Test
    void deleteMenu_callsDao() {
        controller.deleteMenu(12);
        verify(menuDAO).delete(12);
    }

    @Test
    void getMenu_returnsDao() {
        Menu m = new Menu();
        when(menuDAO.getById(15)).thenReturn(m);
        assertSame(m, controller.getMenu(15));
    }
}
