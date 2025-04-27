package com.restaurant.controllers.impl;

import com.restaurant.controllers.UserController;
import com.restaurant.daos.UserDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.dtos.user.LoginUserDto;
import com.restaurant.dtos.user.UpdateUserDto;
import com.restaurant.models.User;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@Injectable
public class UserControllerImpl implements UserController {
    @Inject private UserDAO userDAO;

    public UserControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createUser(CreateUserDto dto) {
        if (userDAO.existsByUsername(dto.getUsername())) {
            System.out.println("Username already taken: " + dto.getUsername());
            return;
        }
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPasswordHash(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        u.setRole(dto.getRole());
        u.setEmail(dto.getEmail());
        u.setActive(true);
        userDAO.add(u);
    }

    @Override
    public void updateUser(UpdateUserDto dto) {
        User u = userDAO.getById(dto.getId());
        if (u == null) return;
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPasswordHash(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        }
        if (dto.getRole() != null) {
            u.setRole(dto.getRole());
        }
        if (dto.getEmail() != null) {
            u.setEmail(dto.getEmail());
        }
        if (dto.isActive() != null) {
            u.setActive(dto.isActive());
        }
        userDAO.update(u);
    }

    @Override
    public User login(LoginUserDto dto) {
        User u = userDAO.findByUsername(dto.getUsername());
        if (u == null || !BCrypt.checkpw(dto.getPassword(), u.getPasswordHash())) {
            System.out.println("Invalid username or password");
            return null;
        }
        return u;
    }

    @Override
    public List<User> findUsers(GetUserDto dto) {
        return userDAO.find(dto);
    }

    @Override
    public User getUser(int id) {
        return userDAO.getById(id);
    }
}