package com.dbizz.repo;

import java.util.List;

import javax.swing.tree.ExpandVetoException;

import com.dbizz.model.OrderItem;

public interface OrderItemRepo {
    public int create(OrderItem orderItem) throws Exception;

    public OrderItem findById(int id) throws Exception;

    public List<OrderItem> findByOrderId(int orderId) throws Exception;

    public List<OrderItem> findByProductId(int productId) throws Exception;

    public List<OrderItem> findAll() throws Exception;

    public int update(OrderItem orderItem) throws Exception;

    public int delete(int id) throws Exception;

}
