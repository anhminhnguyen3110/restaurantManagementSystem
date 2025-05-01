package com.restaurant.controllers;

import com.restaurant.controllers.impl.MenuItemControllerImpl;
import com.restaurant.daos.MenuDAO;
import com.restaurant.daos.MenuItemDAO;
import com.restaurant.dtos.menuItem.CreateMenuItemDto;
import com.restaurant.dtos.menuItem.GetMenuItemsDto;
import com.restaurant.dtos.menuItem.UpdateMenuItemDto;
import com.restaurant.models.Menu;
import com.restaurant.models.MenuItem;
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
class MenuItemControllerImplTest {
    @Mock
    MenuItemDAO menuItemDAO;
    @Mock
    MenuDAO menuDAO;
    @InjectMocks
    MenuItemControllerImpl controller;

    CreateMenuItemDto createDto;
    UpdateMenuItemDto updateDto;
    GetMenuItemsDto getDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateMenuItemDto();
        updateDto = new UpdateMenuItemDto();
        getDto = new GetMenuItemsDto();
    }

    @Test
    void createMenuItem_duplicateName_noAdd() {
        createDto.setName("Burger");
        when(menuItemDAO.existsByName("Burger")).thenReturn(true);
        controller.createMenuItem(createDto);
        verify(menuItemDAO).existsByName("Burger");
        verifyNoMoreInteractions(menuItemDAO, menuDAO);
    }

    @Test
    void createMenuItem_menuNotFound_noAdd() {
        createDto.setName("Pizza");
        createDto.setMenuId(2);
        when(menuItemDAO.existsByName("Pizza")).thenReturn(false);
        when(menuDAO.getById(2)).thenReturn(null);
        controller.createMenuItem(createDto);
        verify(menuItemDAO).existsByName("Pizza");
        verify(menuDAO).getById(2);
        verifyNoMoreInteractions(menuItemDAO, menuDAO);
    }

    @Test
    void createMenuItem_success_addsItem() {
        createDto.setName("Salad");
        createDto.setDescription("Fresh");
        createDto.setPrice(9.99);
        createDto.setMenuId(3);
        when(menuItemDAO.existsByName("Salad")).thenReturn(false);
        Menu menu = new Menu();
        menu.setId(3);
        when(menuDAO.getById(3)).thenReturn(menu);
        controller.createMenuItem(createDto);
        ArgumentCaptor<MenuItem> capt = ArgumentCaptor.forClass(MenuItem.class);
        verify(menuItemDAO).add(capt.capture());
        MenuItem item = capt.getValue();
        assertEquals("Salad", item.getName());
        assertEquals("Fresh", item.getDescription());
        assertEquals(9.99, item.getPrice());
        assertSame(menu, item.getMenu());
    }

    @Test
    void updateMenuItem_notFound_noUpdate() {
        updateDto.setId(5);
        when(menuItemDAO.getById(5)).thenReturn(null);
        controller.updateMenuItem(updateDto);
        verify(menuItemDAO).getById(5);
        verifyNoMoreInteractions(menuItemDAO);
    }

    @Test
    void updateMenuItem_duplicateName_noUpdate() {
        MenuItem existing = new MenuItem();
        existing.setId(6);
        existing.setName("Old");
        when(menuItemDAO.getById(6)).thenReturn(existing);
        updateDto.setId(6);
        updateDto.setName("New");
        when(menuItemDAO.existsByName("New", 6)).thenReturn(true);
        controller.updateMenuItem(updateDto);
        verify(menuItemDAO).getById(6);
        verify(menuItemDAO).existsByName("New", 6);
        verifyNoMoreInteractions(menuItemDAO);
    }

    @Test
    void updateMenuItem_success_updatesItem() {
        MenuItem existing = new MenuItem();
        existing.setId(7);
        existing.setName("A");
        existing.setDescription("B");
        existing.setPrice(1.0);
        when(menuItemDAO.getById(7)).thenReturn(existing);
        updateDto.setId(7);
        updateDto.setName("C");
        updateDto.setDescription("D");
        updateDto.setPrice(2.5);
        when(menuItemDAO.existsByName("C", 7)).thenReturn(false);
        controller.updateMenuItem(updateDto);
        assertEquals("C", existing.getName());
        assertEquals("D", existing.getDescription());
        assertEquals(2.5, existing.getPrice());
        verify(menuItemDAO).update(existing);
    }

    @Test
    void findMenuItems_delegatesToDao() {
        List<MenuItem> list = List.of(new MenuItem(), new MenuItem());
        when(menuItemDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findMenuItems(getDto));
    }

    @Test
    void deleteMenuItem_delegatesToDao() {
        controller.deleteMenuItem(8);
        verify(menuItemDAO).delete(8);
    }

    @Test
    void getMenuItem_returnsFromDao() {
        MenuItem mi = new MenuItem();
        when(menuItemDAO.getById(9)).thenReturn(mi);
        assertSame(mi, controller.getMenuItem(9));
    }

    @Test
    void findMenuItemsByRestaurantId_delegatesToDao() {
        List<MenuItem> list = List.of(new MenuItem());
        when(menuItemDAO.findByRestaurantId(4)).thenReturn(list);
        assertEquals(list, controller.findMenuItemsByRestaurantId(4));
    }
}
