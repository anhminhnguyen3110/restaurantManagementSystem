package com.restaurant.controllers;

import com.restaurant.dtos.user.CreateUserDto;
import com.restaurant.dtos.user.GetUserDto;
import com.restaurant.dtos.user.LoginUserDto;
import com.restaurant.dtos.user.UpdateUserDto;
import com.restaurant.models.User;

import java.util.List;

public interface UserController {
    void createUser(CreateUserDto createUserDto);

    void updateUser(UpdateUserDto updateUserDto);

    User login(LoginUserDto loginUserDto);

    List<User> findUsers(GetUserDto getUserDto);

    User getUser(int id);

    List<User> findAllShippers();
}
