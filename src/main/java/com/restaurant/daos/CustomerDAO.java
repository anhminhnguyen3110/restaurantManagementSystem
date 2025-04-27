package com.restaurant.daos;

import com.restaurant.models.Customer;

import java.util.List;

public interface CustomerDAO {
    void add(Customer customer);

    List<Customer> find();

    void update(Customer customer);

    Customer getByPhoneNumber(String phoneNumber);

    Customer getById(int id);
}
