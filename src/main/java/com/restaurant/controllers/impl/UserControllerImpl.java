package com.restaurant.controllers.impl;

import com.restaurant.controllers.UserController;
import com.restaurant.daos.UserDAO;
import com.restaurant.di.Inject;
import com.restaurant.di.Injectable;
import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.dtos.user.LoginUserDto;
import com.restaurant.dtos.user.UpdateUserDto;
import com.restaurant.events.ErrorEvent;
import com.restaurant.models.User;
import com.restaurant.pubsub.ErrorPubSubService;
import com.restaurant.pubsub.PubSubService;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

@Injectable
public class UserControllerImpl implements UserController {
    private final PubSubService pubSubService = ErrorPubSubService.getInstance();
    @Inject
    private UserDAO userDAO;

    public UserControllerImpl() {
        // Default constructor for DI
    }

    @Override
    public void createUser(CreateUserDto dto) {
        if (userDAO.existsByUsername(dto.getUsername())) {
            pubSubService.publish(new ErrorEvent("Username already taken: " + dto.getUsername()));
            return;
        }
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPasswordHash(BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()));
        u.setRole(dto.getRole());
        u.setEmail(dto.getEmail());
        u.setName(dto.getName());
        u.setActive(true);
        userDAO.add(u);
    }

    @Override
    public void updateUser(UpdateUserDto dto) {
        User u = userDAO.getById(dto.getId());
        if (u == null) {
            pubSubService.publish(new ErrorEvent("User not found: " + dto.getId()));
            return;
        }
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
        if (dto.getName() != null) {
            u.setName(dto.getName());
        }
        userDAO.update(u);
    }

    @Override
    public User login(LoginUserDto dto) {
        User u = userDAO.findByUsername(dto.getUsername());
        if (u == null || !BCrypt.checkpw(dto.getPassword(), u.getPasswordHash())) {
            pubSubService.publish(new ErrorEvent("Invalid username or password"));
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

    @Override
    public List<User> findAllShippers() {
        return userDAO.findAllShippers();
    }

    @Override
    public void deleteUser(int id) {
        userDAO.delete(id);
    }
}
