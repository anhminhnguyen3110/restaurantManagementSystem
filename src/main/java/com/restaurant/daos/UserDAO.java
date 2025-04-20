package com.restaurant.daos;

import com.restaurant.models.User;
import com.restaurant.constants.UserRole;

import java.util.List;

public interface UserDAO {
    void add(User user);

    User getById(int id);

    List<User> findAll();

    User findByUsername(String username);

    List<User> findByRole(UserRole role);

    void update(User user);

    void delete(int id);
}
