package com.dbizz.database.mysql;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.dbizz.model.Product;
import com.dbizz.model.ProductCategory;
import com.dbizz.repo.ProductRepo;

public class MySqlProductRepo implements ProductRepo {

    private DataSource dataSource;

    public MySqlProductRepo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int create(Product product) throws Exception {
        if (product == null)
            throw new IllegalArgumentException("Invalid Product : " + product);
        if (product.name() == null)
            throw new IllegalArgumentException("Invalid Product Name : " + product.name());
        if (product.price() < 0)
            throw new IllegalArgumentException("Negative Product Price : " + product.price());
        if (product.stock() < 0)
            throw new IllegalArgumentException("Negative Product Stock : " + product.stock());
        String sql = """
                INSERT INTO products (name, category,description, price, stock, image_url)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.name());
            stmt.setString(2, product.category().toString());
            stmt.setString(3, product.description());
            stmt.setDouble(4, product.price());
            stmt.setInt(5, product.stock());
            stmt.setString(6, product.imageUrl());
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
    public int update(Product product) throws Exception {
        if (product.id() < 1)
            throw new IllegalArgumentException("Invalid ID : " + product.id());
        if (product.name() == null)
            throw new IllegalArgumentException("Invalid Product Name : " + product.name());
        if (product.price() < 0)
            throw new IllegalArgumentException("Negative Product Price : " + product.price());
        if (product.stock() < 0)
            throw new IllegalArgumentException("Negative Product Stock : " + product.stock());
        String sql = """
                UPDATE products
                SET
                    name = ?,
                    category = ?,
                    description = ?,
                    price = ?,
                    stock = ?
                WHERE id = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.name());
            stmt.setString(2, product.category().toString());
            stmt.setString(3, product.description());
            stmt.setDouble(4, product.price());
            stmt.setInt(5, product.stock());
            stmt.setInt(6, product.id());
            int rows = stmt.executeUpdate();
            if (rows < 1)
                return 0;
            return product.id();
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public Product findById(int id) throws Exception {
        if (id < 1)
            throw new IllegalArgumentException("Invalid ID : " + id);
        String sql = """
                 SELECT *
                FROM products
                WHERE id = ?
                LIMIT 1
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, id);
            ResultSet result = stmt.executeQuery();
            if (!result.next())
                return null;
            return new Product(result.getInt(1),
                    result.getString(2),
                    ProductCategory.fromString(result.getString(3)),
                    result.getString(4),
                    result.getDouble(5),
                    result.getInt(6),
                    result.getString(7),
                    result.getObject(8, LocalDateTime.class));
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<Product> findByName(String name) throws Exception {
        if (name == null)
            throw new IllegalArgumentException("Invalid Product Name : " + name);
        String sql = """
                SELECT * FROM products
                WHERE LOWER(name) LIKE LOWER(CONCAT('%', ?, '%'))
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            ResultSet result = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (result.next()) {
                products.add(new Product(result.getInt(1),
                        result.getString(2),
                        ProductCategory.fromString(result.getString(3)),
                        result.getString(4),
                        result.getDouble(5),
                        result.getInt(6),
                        result.getString(7),
                        result.getObject(8, LocalDateTime.class)));
            }
            return products;
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<Product> findByCategory(ProductCategory category) throws Exception {
        if (category == null)
            throw new IllegalArgumentException("Invalid Product Category : " + category);
        String sql = """
                 SELECT *
                FROM products
                WHERE category = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.toString());
            ResultSet result = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (result.next()) {
                products.add(new Product(result.getInt(1),
                        result.getString(2),
                        ProductCategory.fromString(result.getString(3)),
                        result.getString(4),
                        result.getDouble(5),
                        result.getInt(6),
                        result.getString(7),
                        result.getObject(8, LocalDateTime.class)));
            }
            return products;
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public List<Product> findAll() throws Exception {
        String sql = """
                 SELECT *
                FROM products
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ResultSet result = stmt.executeQuery();
            List<Product> products = new ArrayList<>();
            while (result.next()) {
                products.add(new Product(result.getInt(1),
                        result.getString(2),
                        ProductCategory.fromString(result.getString(3)),
                        result.getString(4),
                        result.getDouble(5),
                        result.getInt(6),
                        result.getString(7),
                        result.getObject(8, LocalDateTime.class)));
            }
            return products;
        } catch (SQLException ex) {
            throw new Exception("SQL Error : " + ex.getMessage());
        }
    }

    @Override
    public int delete(int id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid Id : " + id);
        String sql = """
                DELETE FROM products WHERE id = ?
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

}
