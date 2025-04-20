package com.restaurant.daos;

import com.restaurant.models.Customer;

import java.util.List;

public interface CustomerDAO {
    void add(Customer customer);

    Customer getById(int id);

    List<Customer> findAll();

    Customer findByPhoneNumber(String phoneNumber);

    void update(Customer customer);

    void delete(int id);
}
