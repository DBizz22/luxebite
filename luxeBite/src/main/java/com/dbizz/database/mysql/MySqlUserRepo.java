package com.dbizz.database.mysql;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dbizz.model.User;
import com.dbizz.repo.UserRepo;
import com.dbizz.util.PasswordUtil;

public class MySqlUserRepo implements UserRepo {

    private DataSource dataSource;

    // private Connection getConnection() throws Exception {
    // try {
    // return dataSource.getConnection();
    // } catch (SQLException e) {
    // throw new Exception("SQL Connection Error : " + e.getMessage());
    // }

    // }

    public MySqlUserRepo(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int create(User user) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("Invalid User : " + null);
        String sql = """
                INSERT INTO users (username, email, phone, password)
                VALUES (?, ?, ?, ?)
                        """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, user.username());
            st.setString(2, user.email());
            st.setString(3, user.phoneNo());
            st.setString(4, user.password());
            int rows = st.executeUpdate();
            if (rows < 1)
                throw new Exception("SQL Error : Duplicate Data");
            ResultSet keys = st.getGeneratedKeys();
            keys.next();
            return keys.getInt(1);
        } catch (SQLException e) {
            throw new Exception("SQL Error : " + e.getMessage());
        }
    }

    @Override
    public User findById(int id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Illegal User Id : " + id);
        String sql = """
                SELECT *
                FROM users
                WHERE id = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(sql)) {
            st.setInt(1, id);
            ResultSet result = st.executeQuery();
            if (!result.next())
                return null;
            return new User(result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getObject(6, LocalDateTime.class));
        } catch (SQLException e) {
            throw new Exception("SQL Error : " + e.getMessage());
        }
    }

    @Override
    public List<User> findByUsername(String username) throws Exception {
        if (username == null)
            throw new IllegalArgumentException("Invalid Username : " + username);
        String sql = """
                SELECT *
                FROM users
                WHERE username = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, username);
            ResultSet result = st.executeQuery();
            ArrayList<User> users = new ArrayList<>();
            while (result.next()) {
                users.add(new User(result.getInt(1),
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5),
                        result.getObject(6, LocalDateTime.class)));
            }
            return users;
        } catch (SQLException e) {
            throw new Exception("SQL Error : " + e.getMessage());
        }
    }

    @Override
    public User findByEmail(String email) throws Exception {
        if (email == null)
            throw new IllegalArgumentException("Invalid Email : " + email);
        String sql = """
                SELECT *
                FROM users
                WHERE email = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, email);
            ResultSet result = st.executeQuery();
            if (!result.next())
                return null;
            return new User(result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getObject(6, LocalDateTime.class));
        } catch (SQLException e) {
            throw new Exception("SQL Error : " + e.getMessage());
        }
    }

    @Override
    public User findByPhoneNo(String phoneNo) throws Exception {
        if (phoneNo == null)
            throw new IllegalArgumentException("Invalid PHone Number : " + phoneNo);
        String sql = """
                SELECT *
                FROM users
                WHERE phone = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, phoneNo);
            ResultSet result = st.executeQuery();
            if (!result.next())
                return null;
            return new User(result.getInt(1),
                    result.getString(2),
                    result.getString(3),
                    result.getString(4),
                    result.getString(5),
                    result.getObject(6, LocalDateTime.class));
        } catch (SQLException e) {
            throw new Exception("SQL Error : " + e.getMessage());
        }
    }

    @Override
    public int update(User user) throws Exception {
        if (user == null)
            throw new IllegalArgumentException("Invalid User : " + user);
        String sql = """
                    UPDATE users
                    SET username = ?,
                    email    = ?,
                    phone    = ?,
                    password = ?,
                    createdAt = NOW(6)
                    WHERE id = ?
                """;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement st = conn.prepareStatement(sql)) {
            st.setString(1, user.username());
            st.setString(2, user.email());
            st.setString(3, user.phoneNo());
            st.setString(4, user.password());
            st.setInt(5, user.id());
            int rows = st.executeUpdate();
            if (rows < 1)
                return 0;
            return user.id();
        } catch (SQLException e) {
            throw new Exception("SQL Error : " + e.getMessage());
        }

    }

    @Override
    public int delete(int id) throws Exception {
        if (id <= 0)
            throw new IllegalArgumentException("Invalid Id : " + id);
        String sql = """
                DELETE FROM users WHERE id = ?
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
