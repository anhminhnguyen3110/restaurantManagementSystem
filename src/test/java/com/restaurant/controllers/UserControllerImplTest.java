package com.restaurant.controllers;

import com.restaurant.constants.UserRole;
import com.restaurant.controllers.impl.UserControllerImpl;
import com.restaurant.daos.UserDAO;
import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.dtos.user.LoginUserDto;
import com.restaurant.dtos.user.UpdateUserDto;
import com.restaurant.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {
    @Mock
    UserDAO userDAO;
    @InjectMocks
    UserControllerImpl controller;

    CreateUserDto createDto;
    UpdateUserDto updateDto;
    LoginUserDto loginDto;
    GetUserDto getDto;

    @BeforeEach
    void setUp() {
        createDto = new CreateUserDto();
        updateDto = new UpdateUserDto();
        loginDto = new LoginUserDto();
        getDto = new GetUserDto();
    }

    @Test
    void createUser_usernameTaken_noAdd() {
        createDto.setUsername("john");
        when(userDAO.existsByUsername("john")).thenReturn(true);
        controller.createUser(createDto);
        verify(userDAO).existsByUsername("john");
        verifyNoMoreInteractions(userDAO);
    }

    @Test
    void createUser_success_addsUser() {
        createDto.setUsername("alice");
        createDto.setPassword("pass123");
        createDto.setRole(UserRole.MANAGER);
        createDto.setEmail("e@x.com");
        createDto.setName("Alice");
        when(userDAO.existsByUsername("alice")).thenReturn(false);

        controller.createUser(createDto);

        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        verify(userDAO).add(capt.capture());
        User u = capt.getValue();
        assertEquals("alice", u.getUsername());
        assertTrue(BCrypt.checkpw("pass123", u.getPasswordHash()));
        assertEquals(UserRole.MANAGER, u.getRole());
        assertEquals("e@x.com", u.getEmail());
        assertEquals("Alice", u.getName());
        assertTrue(u.isActive());
    }

    @Test
    void updateUser_notFound_noUpdate() {
        updateDto.setId(5);
        when(userDAO.getById(5)).thenReturn(null);
        controller.updateUser(updateDto);
        verify(userDAO).getById(5);
        verifyNoMoreInteractions(userDAO);
    }

    @Test
    void updateUser_withPasswordAndFields_updates() {
        User u = new User();
        u.setId(6);
        u.setPasswordHash(BCrypt.hashpw("old", BCrypt.gensalt()));
        u.setRole(UserRole.WAIT_STAFF);
        u.setEmail("old@e.com");
        u.setName("Old");
        u.setActive(false);
        when(userDAO.getById(6)).thenReturn(u);

        updateDto.setId(6);
        updateDto.setPassword("newpass");
        updateDto.setRole(UserRole.OWNER);
        updateDto.setEmail("new@e.com");
        updateDto.setName("NewName");
        updateDto.setActive(true);

        controller.updateUser(updateDto);

        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        verify(userDAO).update(capt.capture());
        User updated = capt.getValue();
        assertTrue(BCrypt.checkpw("newpass", updated.getPasswordHash()));
        assertEquals(UserRole.OWNER, updated.getRole());
        assertEquals("new@e.com", updated.getEmail());
        assertEquals("NewName", updated.getName());
        assertTrue(updated.isActive());
    }

    @Test
    void updateUser_withoutPassword_keepsOldHash() {
        User u = new User();
        u.setId(7);
        String oldHash = BCrypt.hashpw("secret", BCrypt.gensalt());
        u.setPasswordHash(oldHash);
        when(userDAO.getById(7)).thenReturn(u);

        updateDto.setId(7);
        updateDto.setPassword("");
        controller.updateUser(updateDto);

        ArgumentCaptor<User> capt = ArgumentCaptor.forClass(User.class);
        verify(userDAO).update(capt.capture());
        User updated = capt.getValue();
        assertEquals(oldHash, updated.getPasswordHash());
    }

    @Test
    void login_userNotFound_returnsNull() {
        loginDto.setUsername("x");
        loginDto.setPassword("p");
        when(userDAO.findByUsername("x")).thenReturn(null);
        assertNull(controller.login(loginDto));
    }

    @Test
    void login_wrongPassword_returnsNull() {
        loginDto.setUsername("u");
        loginDto.setPassword("bad");
        User u = new User();
        u.setPasswordHash(BCrypt.hashpw("good", BCrypt.gensalt()));
        when(userDAO.findByUsername("u")).thenReturn(u);
        assertNull(controller.login(loginDto));
    }

    @Test
    void login_success_returnsUser() {
        loginDto.setUsername("bob");
        loginDto.setPassword("mypw");
        User u = new User();
        u.setPasswordHash(BCrypt.hashpw("mypw", BCrypt.gensalt()));
        when(userDAO.findByUsername("bob")).thenReturn(u);
        assertSame(u, controller.login(loginDto));
    }

    @Test
    void findUsers_delegatesToDao() {
        List<User> list = List.of(new User(), new User());
        when(userDAO.find(getDto)).thenReturn(list);
        assertEquals(list, controller.findUsers(getDto));
    }

    @Test
    void getUser_delegatesToDao() {
        User u = new User();
        when(userDAO.getById(8)).thenReturn(u);
        assertSame(u, controller.getUser(8));
    }

    @Test
    void findAllShippers_delegatesToDao() {
        List<User> list = List.of(new User());
        when(userDAO.findAllShippers()).thenReturn(list);
        assertEquals(list, controller.findAllShippers());
    }

    @Test
    void deleteUser_delegatesToDao() {
        controller.deleteUser(9);
        verify(userDAO).delete(9);
    }
}
