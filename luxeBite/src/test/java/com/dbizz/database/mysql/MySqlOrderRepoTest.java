package com.dbizz.database.mysql;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

import java.sql.*;
import java.util.List;
import java.util.function.Predicate;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.dbizz.database.AbstractMySQLBase;
import com.dbizz.database.mysql.MySqlOrderRepo;
import com.dbizz.model.Order;
import com.dbizz.model.PaymentStatus;
import com.dbizz.model.Product;
import com.dbizz.repo.OrderRepo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySqlOrderRepoTest extends AbstractMySQLBase {

    private static DataSource dataSource;
    private static OrderRepo orderDAO;
    private static Order pendingOrder = new Order(1, 1, PaymentStatus.PENDING, null);
    private static Order completedOrder = new Order(2, 1, PaymentStatus.COMPLETED, null);
    private static Order extraOrder = new Order(3, 2, PaymentStatus.PENDING, null);

    @BeforeAll
    public static void setup() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl());
        config.setUsername(getUsername());
        config.setPassword(getPassword());
        config.setLeakDetectionThreshold(2000);

        dataSource = new HikariDataSource(config);
        orderDAO = new MySqlOrderRepo(dataSource);
        // product = new Product(1, "Fried Eggs", "Best fried eggs", 5, 20, null);
        System.out.println("Running ProductDAOmysql Test....");
    }

    @BeforeEach
    public void createTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS orders (
                        id         BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                        userId     BIGINT UNSIGNED NOT NULL,
                        status     VARCHAR(20) DEFAULT 'pending',
                        createdAt TIMESTAMP DEFAULT (UTC_TIMESTAMP()),

                        INDEX idx_userid (userId)

                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
                                        """);
        }
    }

    @AfterEach
    public void truncateTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();) {
            st.execute("TRUNCATE TABLE orders");
        }
    }

    @Test
    public void createOrder_whenOrderIsNull_throwsException() {
        Order nullOrder = null;
        assertThrows(Exception.class, () -> {
            orderDAO.create(nullOrder);
        });

        // assertThat(ex.getMessage()).isEqualTo("Invalid ID");
    }

    @Test
    public void createOrder_whenUserIdIsInvalid_throwsException() {
        Order invalidUserIdOrder = new Order(pendingOrder.id(), 0, pendingOrder.status(), null);
        assertThrows(Exception.class, () -> {
            orderDAO.create(invalidUserIdOrder);
        });

        Order invalidUserIdOrder2 = new Order(pendingOrder.id(), -2, pendingOrder.status(), null);
        assertThrows(Exception.class, () -> {
            orderDAO.create(invalidUserIdOrder2);
        });

        // assertThat(ex.getMessage()).isEqualTo("Invalid ID");
    }

    @Test
    public void createOrder_whenPaymentStatusIsNull_throwsException() {
        Order invalidPaymentStatusOrder = new Order(pendingOrder.id(), pendingOrder.userId(), null,
                null);
        assertThrows(Exception.class, () -> {
            orderDAO.create(invalidPaymentStatusOrder);
        });
    }

    // @Test
    // public void createOrder_whenOrderTotalIsNegative_throwsException() {
    // Order negativeTotalOrder = new Order(order.id(), order.userId(),
    // order.status(), -50, null);
    // assertThrows(Exception.class, () -> {
    // orderDAO.create(negativeTotalOrder);
    // });
    // }

    @Test
    public void createOrder_whenOrderIsValid_doesNotThrowException_returnsNewOrderId() {
        int id = assertDoesNotThrow(() -> {
            return orderDAO.create(pendingOrder);
        });
        assertTrue(id > 0);
    }

    @Test
    public void findById_whenOrderIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            orderDAO.findById(-2);
        });

        assertThrows(Exception.class, () -> {
            orderDAO.findById(0);
        });
    }

    @Test
    public void findById_whenOrderIdDoesNotExist_returnsNull() {
        Order searchedOrder = assertDoesNotThrow(() -> {
            return orderDAO.findById(pendingOrder.id());
        });
        assertNull(searchedOrder);
    }

    @Test
    public void findById_whenOrderIdExists_returnsMatchedOrder() {
        int id = assertDoesNotThrow(() -> {
            return orderDAO.create(pendingOrder);
        });
        Order searchedOrder = assertDoesNotThrow(() -> {
            return orderDAO.findById(id);
        });
        assertEquals(id, searchedOrder.id());
        assertEquals(pendingOrder.userId(), searchedOrder.userId());
        assertEquals(pendingOrder.status(), searchedOrder.status());
        // assertEquals(order.total(), searchedOrder.total());
    }

    @Test
    public void findByUserId_whenUserIdIsInvalid_throwsException() {
        assertThatThrownBy(() -> {
            orderDAO.findByUserId(0);
        }).isInstanceOf(Exception.class);
        assertThatThrownBy(() -> {
            orderDAO.findByUserId(-2);
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void findByUserId_whenUserIdDoesNotExist_returnsEmptyList() {
        List<Order> orders = assertDoesNotThrow(() -> {
            return orderDAO.findByUserId(pendingOrder.userId());
        });
        assertThat(orders).isEmpty();
    }

    @Test
    public void findByUserId_whenUserIdExists_returnsAllMAtchedOrders() {

        List<Order> orders = assertDoesNotThrow(() -> {
            orderDAO.create(pendingOrder);
            orderDAO.create(extraOrder);
            return orderDAO.findByUserId(pendingOrder.userId());
        });

        Predicate<Order> isOrder1 = s1 -> s1.id() == pendingOrder.id() &&
                s1.userId() == pendingOrder.userId() &&
                s1.status() == pendingOrder.status(); // &&
        // s1.total() == order.total();

        // Predicate<Order> isOrder2 = s1 -> s1.id() == extraOrder.id() &&
        // s1.userId() == extraOrder.userId() &&
        // s1.status() == extraOrder.status(); // &&
        // s1.total() == extraOrder.total();

        assertThat(orders).hasSize(1)
                .anyMatch(isOrder1);

    }

    // TODO: find by status

    @Test
    public void findByStatus_whenInvalidUserId_throwsException() {
        assertThrows(Exception.class, () -> {
            orderDAO.findByStatus(-1, PaymentStatus.COMPLETED);
        });

        assertThrows(Exception.class, () -> {
            orderDAO.findByStatus(0, PaymentStatus.COMPLETED);
        });
    }

    @Test
    public void findByStatus_whenPaymentStatusIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            orderDAO.findByStatus(pendingOrder.userId(), null);
        });
    }

    @Test
    public void findByStatus_whenNoPaymentStatusIsPending_returnsEmptyList() {

        List<Order> actualOrders = assertDoesNotThrow(() -> {
            orderDAO.create(extraOrder);
            return orderDAO.findByStatus(pendingOrder.userId(), PaymentStatus.PENDING);
        });
        assertThat(actualOrders).isEmpty();
    }

    @Test
    public void findByStatus_whenPaymentStatusIsPending_returnsListOfPendingOrders() {
        int id = assertDoesNotThrow(() -> {
            orderDAO.create(extraOrder);
            orderDAO.create(completedOrder);
            return orderDAO.create(pendingOrder);
        });

        List<Order> actualOrders = assertDoesNotThrow(() -> {
            return orderDAO.findByStatus(pendingOrder.userId(), PaymentStatus.PENDING);
        });

        assertThat(actualOrders).hasSize(1);
        assertEquals(id, actualOrders.get(0).id());
        assertEquals(pendingOrder.userId(), actualOrders.get(0).userId());
        assertEquals(pendingOrder.status(), actualOrders.get(0).status());
        // assertEquals(order.total(), searchedOrder.total());
    }

    @Test
    public void findByStatus_whenNoPaymentStatusIsCompleted_returnsEmptyList() {
        List<Order> actualOrders = assertDoesNotThrow(() -> {
            orderDAO.create(extraOrder);
            return orderDAO.findByStatus(completedOrder.userId(), PaymentStatus.COMPLETED);
        });
        assertThat(actualOrders).isEmpty();
    }

    @Test
    public void findByStatus_whenPaymentStatusIsCompleted_returnsListOfCompletedOrders() {
        int id = assertDoesNotThrow(() -> {
            orderDAO.create(extraOrder);
            orderDAO.create(pendingOrder);
            return orderDAO.create(completedOrder);
        });

        List<Order> actualOrders = assertDoesNotThrow(() -> {
            return orderDAO.findByStatus(completedOrder.userId(), PaymentStatus.COMPLETED);
        });

        assertThat(actualOrders).hasSize(1);
        assertEquals(id, actualOrders.get(0).id());
        assertEquals(completedOrder.userId(), actualOrders.get(0).userId());
        assertEquals(completedOrder.status(), actualOrders.get(0).status());
    }

    // TODO

    @Test
    public void updateOrder_whenOrderIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            orderDAO.update(null);
        });
    }

    @Test
    public void updateOrder_whenOrderIdIsInvalid_throwsException() {
        Order invalidIdOrder = new Order(0, pendingOrder.userId(), pendingOrder.status(), null);
        assertThrows(Exception.class, () -> {
            orderDAO.update(invalidIdOrder);
        });

        Order invalidIdOrder2 = new Order(-2, pendingOrder.userId(), pendingOrder.status(), null);
        assertThrows(Exception.class, () -> {
            orderDAO.update(invalidIdOrder2);
        });
    }

    @Test
    public void updateOrder_whenOrderIdDoesNotExist_doesNotThrowException_returnsZeroId() {
        int id = assertDoesNotThrow(() -> {
            return orderDAO.update(pendingOrder);
        });
        assertEquals(0, id);
    }

    @Test
    public void updateOrder_whenUserIdIsInvalid_throwsException() {
        Order invalidUserIdOrder = new Order(pendingOrder.id(), 0, pendingOrder.status(), null);
        assertThrows(Exception.class, () -> {
            orderDAO.update(invalidUserIdOrder);
        });

        Order invalidUserIdOrder2 = new Order(-2, pendingOrder.userId(), pendingOrder.status(), null);
        assertThrows(Exception.class, () -> {
            orderDAO.update(invalidUserIdOrder2);
        });
    }

    // @Test
    // public void updateOrder_whenOrderTotalIsNegative_throwsException() {
    // Order negativeTotalOrder = new Order(order.id(), order.userId(),
    // order.status(), -50, null);
    // assertThrows(Exception.class, () -> {
    // orderDAO.update(negativeTotalOrder);
    // });
    // }

    @Test
    public void updateOrder_whenOrderIdExists_returnsUpdatedOrderId_findById_returnsMatchedOrder() {
        int id = assertDoesNotThrow(() -> {
            return orderDAO.create(pendingOrder);
        });
        Order updatedOrder = new Order(id, pendingOrder.userId(), PaymentStatus.COMPLETED, null);
        int updatedId = assertDoesNotThrow(() -> {
            return orderDAO.update(updatedOrder);
        });
        assertEquals(id, updatedId);

        Order matchedOrder = assertDoesNotThrow(() -> {
            return orderDAO.findById(updatedId);
        });

        assertEquals(updatedOrder.id(), matchedOrder.id());
        assertEquals(updatedOrder.userId(), matchedOrder.userId());
        assertEquals(updatedOrder.status(), matchedOrder.status());
        // assertEquals(updatedOrder.total(), matchedOrder.total());
    }

    @Test
    public void deleteOrder_whenOrderIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            orderDAO.delete(0);
        });

        assertThrows(Exception.class, () -> {
            orderDAO.delete(-2);
        });
    }

    @Test
    public void deleteOrder_whenIdDoesNotExist_returnsZeroId() {
        int id = assertDoesNotThrow(() -> {
            return orderDAO.delete(1);
        });
        assertEquals(0, id);
    }

    @Test
    public void deleteOrder_whenIdExists_returnsDeletedId_findById_returnsNull() {
        int id = assertDoesNotThrow(() -> {
            return orderDAO.create(pendingOrder);
        });
        int deletedId = assertDoesNotThrow(() -> {
            return orderDAO.delete(id);
        });
        assertEquals(id, deletedId);
        Order deletedOrder = assertDoesNotThrow(() -> {
            return orderDAO.findById(deletedId);
        });
        assertNull(deletedOrder);
    }

}
