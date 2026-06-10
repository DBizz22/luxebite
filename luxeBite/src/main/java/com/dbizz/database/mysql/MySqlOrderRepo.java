package com.dbizz.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.dbizz.model.Order;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.Product;
import com.dbizz.repo.OrderRepo;

public class MySqlOrderRepo implements OrderRepo {

    private DataSource dataSource;

    public MySqlOrderRepo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int create(Order order) throws Exception {
        if (order == null)
            throw new IllegalArgumentException("Invalid Product : " + order);
        if (order.userId() < 1)
            throw new IllegalArgumentException("Invalid User ID : " + order.userId());
        if (order.status() == null)
            throw new IllegalArgumentException("Invalid Payment Status : " + order.status());

        String sql = """
                INSERT INTO orders (userId, status)
                VALUES (?, ?)
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.userId());
            stmt.setString(2, order.status().toString());
            // stmt.setDouble(3, order.total());
            int rows = stmt.executeUpdate();
            if (rows < 1)
                return 0;
            ResultSet result = stmt.getGeneratedKeys();
            result.next();
            return result.getInt(1);
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public Order findById(int id) throws Exception {
        if (id < 1)
            throw new IllegalArgumentException("Invalid ID : " + id);
        String sql = """
                 SELECT *
                FROM orders
                WHERE id = ?
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (!result.next())
                return null;
            return new Order(result.getInt(1),
                    result.getInt(2),
                    PaymentStatus.toStatus(result.getString(3)),
                    result.getObject(4, LocalDateTime.class));
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<Order> findByUserId(int id) throws Exception {
        if (id < 1)
            throw new IllegalArgumentException("Invalid ID : " + id);
        String sql = """
                 SELECT *
                FROM orders
                WHERE userId = ?
                ORDER BY createdAt DESC;
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (result.next()) {
                orders.add(new Order(result.getInt(1),
                        result.getInt(2),
                        PaymentStatus.toStatus(result.getString(3)),
                        result.getObject(4, LocalDateTime.class)));
            }
            return orders;
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<Order> findByStatus(int userId, PaymentStatus status) throws Exception {
        if (userId <= 0)
            throw new IllegalArgumentException("Invalid User ID : " + userId);
        if (status == null)
            throw new IllegalArgumentException("Invalid Payment Status : " + status);
        String sql = """
                 SELECT *
                FROM orders
                WHERE status = ? AND userId = ?
                ORDER BY createdAt DESC;
                """;

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, status.toString());
            stmt.setInt(2, userId);
            ResultSet result = stmt.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (result.next()) {
                orders.add(new Order(result.getInt(1),
                        result.getInt(2),
                        PaymentStatus.toStatus(result.getString(3)),
                        result.getObject(4, LocalDateTime.class)));
            }
            return orders;
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public int delete(int id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid Id : " + id);
        String sql = """
                DELETE FROM orders WHERE id = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            int rows = st.executeUpdate();
            if (rows < 1)
                return 0;
            return id;
        } catch (SQLException e) {
            throw new Exception("SQL Error : " + e.getMessage());
        }
    }

    @Override
    public int update(Order order) throws Exception {
        if (order == null)
            throw new IllegalArgumentException("Invalid Product : " + order);
        if (order.id() < 1)
            throw new IllegalArgumentException("Invalid Order ID : " + order.id());
        if (order.userId() < 1)
            throw new IllegalArgumentException("Invalid User ID : " + order.id());
        if (order.status() == null)
            throw new IllegalArgumentException("Invalid Payment Status : " + order.status());
        String sql = """
                UPDATE orders
                SET
                    userId = ?,
                    status = ?
                WHERE id = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.userId());
            stmt.setString(2, order.status().toString());
            // stmt.setDouble(3, order.total());
            stmt.setInt(3, order.id());
            int rows = stmt.executeUpdate();
            if (rows < 1)
                return 0;
            return order.id();
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

}
