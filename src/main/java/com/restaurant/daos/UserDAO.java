package com.restaurant.daos;

import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.models.User;

import java.util.List;

public interface UserDAO {
    void add(User user);

    User getById(int id);

    List<User> find(GetUserDto dto);

    User findByUsername(String username);

    void update(User user);

    void delete(int id);

    boolean existsByUsername(String username);

    List<User> findAllShippers();
}