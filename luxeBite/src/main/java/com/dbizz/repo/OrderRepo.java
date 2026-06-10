package com.dbizz.repo;

import java.util.List;

import com.dbizz.model.Order;
import com.dbizz.model.PaymentStatus;

public interface OrderRepo {
    public int create(Order order) throws Exception;

    public Order findById(int id) throws Exception;

    public List<Order> findByUserId(int userId) throws Exception;

    public List<Order> findByStatus(int userId, PaymentStatus status) throws Exception;

    public int update(Order order) throws Exception;

    public int delete(int id) throws Exception;
}
