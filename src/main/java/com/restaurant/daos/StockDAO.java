package com.restaurant.daos;

import com.restaurant.models.Stock;
import com.restaurant.models.Supplier;

import java.util.List;

public interface StockDAO {
    void add(Stock stock);

    Stock getById(int id);

    List<Stock> findAll();

    List<Stock> findBySupplier(Supplier supplier);

    List<Stock> findLowStock();

    void update(Stock stock);

    void delete(int id);
}