package com.restaurant.daos;

import com.restaurant.models.Supplier;

import java.util.List;

public interface SupplierDAO {
    void add(Supplier supplier);

    Supplier getById(int id);

    List<Supplier> findAll();

    Supplier findByName(String name);

    List<Supplier> findByAddress(String address);

    List<Supplier> findByEmail(String email);

    List<Supplier> findByPhone(String phone);

    void update(Supplier supplier);

    void delete(int id);
}
