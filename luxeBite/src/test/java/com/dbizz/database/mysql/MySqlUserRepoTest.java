package com.dbizz.database.mysql;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Executable;
import java.sql.*;
import java.util.List;
import java.util.function.Predicate;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.dbizz.database.AbstractMySQLBase;
import com.dbizz.database.mysql.MySqlUserRepo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.dbizz.model.Order;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.User;
import com.dbizz.repo.UserRepo;

public class MySqlUserRepoTest extends AbstractMySQLBase {

    private static DataSource dataSource;
    private static UserRepo userDAO;
    private static User user;

    @BeforeAll
    public static void setup() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl());
        config.setUsername(getUsername());
        config.setPassword(getPassword());
        config.setLeakDetectionThreshold(2000);

        dataSource = new HikariDataSource(config);
        userDAO = new MySqlUserRepo(dataSource);
        user = new User(1, "nameTest", "mail@gmail.com", "+8613185084844", "password", null);
    }

    // TODO: optimize tables
    @BeforeEach
    public void createTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute("""
                                        CREATE TABLE IF NOT EXISTS users (
                        id              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                        username        VARCHAR(50)  NOT NULL,
                        email           VARCHAR(255) NOT NULL,
                        phone           VARCHAR(20)  NOT NULL,
                        password        VARCHAR(255) NOT NULL,
                        createdAt       DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),

                        UNIQUE KEY uk_email (email),
                        UNIQUE KEY uk_phone (phone),

                        INDEX idx_email (email),
                        INDEX idx_phone (phone),
                        INDEX idx_username (username)

                    ) ENGINE=InnoDB
                      DEFAULT CHARSET=utf8mb4
                      COLLATE=utf8mb4_0900_ai_ci
                                        """);
        }
    }

    @AfterEach
    public void truncateTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();) {
            st.execute("TRUNCATE TABLE users");
        }
    }

    // TODO: implement parameters check
    @Test
    public void createUser_whenUserIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userDAO.create(null);
        });
    }

    @Test
    public void createUser_whenValidUser_doesNotThrowException_returnsPositiveId() {
        int id = assertDoesNotThrow(() -> {
            return userDAO.create(user);
        });
        assertTrue(id > 0);
    }

    @Test
    public void createUser_whenDuplicateEmail_throwsException() {
        User duplicateEmailUser = new User(1, user.username(), user.email(), "+8618340322352", "password", null);
        assertThrows(Exception.class, () -> {
            userDAO.create(user);
            userDAO.create(duplicateEmailUser);
        });
    }

    @Test
    public void createUser_whenDuplicatePhoneNo_throwsException() {
        User duplicatePhoneNoUser = new User(1, user.username(), "divinesamuel1515@gmail.com", user.phoneNo(),
                "password", null);
        assertThrows(Exception.class, () -> {
            userDAO.create(user);
            userDAO.create(duplicatePhoneNoUser);
        });
    }

    @Test
    public void createUser_whenDuplicateEmailAndPhoneNo_throwsException() {
        User duplicateEmailAndPhoneNoUser = new User(1, user.username(), user.email(), user.phoneNo(),
                "password", null);
        assertThrows(Exception.class, () -> {
            userDAO.create(user);
            userDAO.create(duplicateEmailAndPhoneNoUser);
        });
    }

    @Test
    public void createUser_whenDifferentEmailAndPhoneNo_doesNotThrowException() {
        User differentEmailAndPhoneNoUser = new User(1, user.username(),
                "divinesamuel1515@gmail.com",
                "+8618340322352",
                "password", null);

        assertDoesNotThrow(() -> {
            userDAO.create(user);
            userDAO.create(differentEmailAndPhoneNoUser);
        });
    }

    @Test
    public void findById_whenIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            userDAO.findById(0);
        });
    }

    @Test
    public void findById_whenIdDoesNotExist_returnsNull() {
        User invalidUser = assertDoesNotThrow(() -> {
            return userDAO.findById(5);
        });
        assertNull(invalidUser);
    }

    @Test
    public void findById_whenIdExists_returnsMatchedUser() {
        int id = assertDoesNotThrow(() -> {
            return userDAO.create(user);
        });
        User searchedUser = assertDoesNotThrow(() -> {
            return userDAO.findById(id);
        });
        assertEquals(searchedUser.id(), id);
        assertEquals(searchedUser.username(), user.username());
        assertEquals(searchedUser.email(), user.email());
        assertEquals(searchedUser.phoneNo(), user.phoneNo());
        assertEquals(searchedUser.password(), user.password());
    }

    @Test
    public void findByUsername_whenUsernameIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userDAO.findByUsername(null);
        });
    }

    @Test
    public void findByUsername_whenUsernameDoesNotExist_returnsEmptyList() {
        List<User> users = assertDoesNotThrow(() -> {
            return userDAO.findByUsername(user.username());
        });
        assertThat(users).isEmpty();
    }

    @Test
    public void findByUserId_whenUserIdExists_returnsAllMAtchedOrders() {

        User extraUser = new User(2, user.username(), "email2@gmail.com", "1342334565", "12334455", null);
        int id1 = assertDoesNotThrow(() -> {
            return userDAO.create(user);
        });

        int id2 = assertDoesNotThrow(() -> {
            return userDAO.create(extraUser);
        });

        List<User> users = assertDoesNotThrow(() -> {
            return userDAO.findByUsername(user.username());
        });

        Predicate<User> isUser1 = s1 -> s1.id() == id1 &&
                s1.username().equals(user.username()) &&
                s1.email().equals(user.email()) &&
                s1.phoneNo().equals(user.phoneNo()) &&
                s1.password().equals(user.password());

        Predicate<User> isUser2 = s1 -> s1.id() == id2 &&
                s1.username().equals(extraUser.username()) &&
                s1.email().equals(extraUser.email()) &&
                s1.phoneNo().equals(extraUser.phoneNo()) &&
                s1.password().equals(extraUser.password());

        assertThat(users).hasSize(2)
                .anyMatch(isUser1)
                .anyMatch(isUser2);

    }

    @Test
    public void findByEmail_whenEmailIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userDAO.findByEmail(null);
        });
    }

    // @Test
    // public void findByEmail_whenEmailIsInvalid_throwsException() {
    // assertThrows(Exception.class, () -> {
    // userDAO.findByEmail("abcdefghijkl");
    // });
    // }

    @Test
    public void findByEmail_whenEmailDoesNotExist_returnsNull() {
        User searchedUser = assertDoesNotThrow(() -> {
            return userDAO.findByEmail(user.email());
        });
        assertNull(searchedUser);
    }

    @Test
    public void findByEmail_whenEmailExists_returnsMatchedUser() {
        User searchedUser = assertDoesNotThrow(() -> {
            userDAO.create(user);
            return userDAO.findByEmail(user.email());
        });
        assertEquals(searchedUser.username(), user.username());
        assertEquals(searchedUser.email(), user.email());
        assertEquals(searchedUser.phoneNo(), user.phoneNo());
        assertEquals(searchedUser.password(), user.password());
    }

    @Test
    public void findByPhoneNo_whenPhoneNoIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userDAO.findByPhoneNo(null);
        });
    }

    // @Test
    // public void findByPhoneNo_whenPhoneNoIsInvalid_throwsException() {
    // assertThrows(Exception.class, () -> {
    // userDAO.findByPhoneNo("1834032235a");
    // });
    // }

    @Test
    public void findByPhoneNo_whenPhoneNoDoesNotExist_returnsNull() {
        User searchedUser = assertDoesNotThrow(() -> {
            return userDAO.findByPhoneNo(user.phoneNo());
        });
        assertNull(searchedUser);
    }

    @Test
    public void findByPhoneNo_whenPhoneNoExists_returnsMatchedUser() {
        User searchedUser = assertDoesNotThrow(() -> {
            userDAO.create(user);
            return userDAO.findByPhoneNo(user.phoneNo());
        });
        assertEquals(searchedUser.username(), user.username());
        assertEquals(searchedUser.email(), user.email());
        assertEquals(searchedUser.phoneNo(), user.phoneNo());
        assertEquals(searchedUser.password(), user.password());
    }

    @Test
    public void updateUser_whenUserIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            userDAO.update(null);
        });
    }

    @Test
    public void updateUser_whenUserDoesNotExist_doesNotThrowException_returnsZeroId() {
        int id = assertDoesNotThrow(() -> {
            return userDAO.update(user);
        });
        assertEquals(0, id);
    }

    @Test
    public void updateUser_whenUserExists_returnsUpdatedId_findById_returnsUpdatedUser() {
        int id = assertDoesNotThrow(() -> {
            return userDAO.create(user);
        });
        User updatedUser = new User(id, "updatedUsername", "updatedEmail",
                "updatedPhoneNo",
                "Updatedpassword", null);
        int updatedId = assertDoesNotThrow(() -> {
            return userDAO.update(updatedUser);
        });
        User expectedUpdatedUser = assertDoesNotThrow(() -> {
            return userDAO.findById(updatedId);
        });
        assertEquals(expectedUpdatedUser.id(), updatedUser.id());
        assertEquals(expectedUpdatedUser.username(), updatedUser.username());
        assertEquals(expectedUpdatedUser.email(), updatedUser.email());
        assertEquals(expectedUpdatedUser.phoneNo(), updatedUser.phoneNo());
        assertEquals(expectedUpdatedUser.password(), updatedUser.password());
    }

    @Test
    public void deleteUser_whenIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            userDAO.delete(0);
        });
    }

    @Test
    public void deleteUser_whenIdDoesNotExist_returnsZeroId() {
        int id = assertDoesNotThrow(() -> {
            return userDAO.delete(1);
        });
        assertEquals(0, id);
    }

    @Test
    public void deleteUser_whenIdExists_returnsDeletedId_findById_returnsNull() {
        int id = assertDoesNotThrow(() -> {
            return userDAO.create(user);
        });
        int deletedId = assertDoesNotThrow(() -> {
            return userDAO.delete(id);
        });
        assertEquals(id, deletedId);
        User deletedUser = assertDoesNotThrow(() -> {
            return userDAO.findById(deletedId);
        });
        assertNull(deletedUser);
    }

    // @AfterAll

}
