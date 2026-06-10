package com.dbizz.database.mysql;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.dbizz.model.OrderItem;
import com.dbizz.repo.OrderItemRepo;

public class MySqlOrderItemRepo implements OrderItemRepo {

    private DataSource dataSource;

    public MySqlOrderItemRepo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int create(OrderItem orderItem) throws Exception {
        if (orderItem == null)
            throw new IllegalArgumentException("Invalid Order Item : " + orderItem);
        if (orderItem.orderId() < 1)
            throw new IllegalArgumentException("Invalid Order ID : " + orderItem.orderId());
        if (orderItem.productId() < 1)
            throw new IllegalArgumentException("Invalid Product ID : " + orderItem.productId());
        if (orderItem.unitPrice() < 0)
            throw new IllegalArgumentException("Invalid Unit Price : " + orderItem.unitPrice());
        if (orderItem.quantity() < 1)
            throw new IllegalArgumentException("Invalid Quantity : " + orderItem.quantity());
        String sql = """
                INSERT INTO order_items (orderId, productId, unitPrice, quantity)
                VALUES (?, ?, ?, ?)
                """;
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, orderItem.orderId());
            stmt.setInt(2, orderItem.productId());
            stmt.setDouble(3, orderItem.unitPrice());
            stmt.setInt(4, orderItem.quantity());
            int rows = stmt.executeUpdate();
            if (rows < 1)
                return 0;
            var result = stmt.getGeneratedKeys();
            result.next();
            return result.getInt(1);
        } catch (Exception ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public OrderItem findById(int id) throws Exception {
        if (id < 1)
            throw new IllegalArgumentException("Invalid Order Item ID : " + id);
        String sql = """
                SELECT * FROM order_items WHERE id = ?
                """;
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return new OrderItem(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rs.getInt(5),
                        rs.getObject(7, LocalDateTime.class));
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) throws Exception {
        if (orderId < 1)
            throw new IllegalArgumentException("Invalid Order ID : " + orderId);
        String sql = """
                SELECT * FROM order_items WHERE orderId = ? ORDER BY createdAt DESC
                """;
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            var rs = stmt.executeQuery();
            var orderItems = new ArrayList<OrderItem>();
            while (rs.next()) {
                orderItems.add(new OrderItem(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rs.getInt(5),
                        rs.getObject(7, LocalDateTime.class)));
            }
            return orderItems;
        } catch (Exception ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<OrderItem> findByProductId(int productId) throws Exception {
        if (productId < 1)
            throw new IllegalArgumentException("Invalid Product ID : " + productId);
        String sql = """
                SELECT * FROM order_items WHERE productId = ? ORDER BY createdAt DESC
                """;
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            var rs = stmt.executeQuery();
            List<OrderItem> orderItems = new ArrayList<>();
            while (rs.next()) {
                orderItems.add(new OrderItem(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rs.getInt(5),
                        rs.getObject(7, LocalDateTime.class)));
            }
            return orderItems;
        } catch (Exception ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<OrderItem> findAll() throws Exception {
        String sql = """
                SELECT * FROM order_items ORDER BY createdAt DESC
                """;
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            List<OrderItem> orderItems = new ArrayList<>();
            while (rs.next()) {
                orderItems.add(new OrderItem(
                        rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDouble(4),
                        rs.getInt(5),
                        rs.getObject(7, LocalDateTime.class)));
            }
            return orderItems;
        } catch (Exception ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public int update(OrderItem orderItem) throws Exception {
        if (orderItem == null)
            throw new IllegalArgumentException("Invalid Order Item : " + orderItem);
        if (orderItem.id() < 1)
            throw new IllegalArgumentException("Invalid Order Item ID : " + orderItem.id());
        if (orderItem.orderId() < 1)
            throw new IllegalArgumentException("Invalid Order ID : " + orderItem.orderId());
        if (orderItem.productId() < 1)
            throw new IllegalArgumentException("Invalid Product ID : " + orderItem.productId());
        if (orderItem.unitPrice() < 0)
            throw new IllegalArgumentException("Invalid Unit Price : " + orderItem.unitPrice());
        if (orderItem.quantity() < 1)
            throw new IllegalArgumentException("Invalid Quantity : " + orderItem.quantity());
        String sql = """
                UPDATE order_items
                SET orderId = ?, productId = ?, unitPrice = ?, quantity = ?
                WHERE id = ?
                """;
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderItem.orderId());
            stmt.setInt(2, orderItem.productId());
            stmt.setDouble(3, orderItem.unitPrice());
            stmt.setInt(4, orderItem.quantity());
            stmt.setInt(5, orderItem.id());
            int rows = stmt.executeUpdate();
            if (rows < 1)
                return 0;
            return orderItem.id();
        } catch (Exception ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public int delete(int id) throws Exception {
        if (id < 1)
            throw new IllegalArgumentException("Invalid Order Item ID : " + id);
        String sql = """
                DELETE FROM order_items WHERE id = ?
                """;
        try (var conn = dataSource.getConnection();
                var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows < 1)
                return 0;
            return id;
        } catch (Exception ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

}
