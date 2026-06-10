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
import com.dbizz.database.mysql.MySqlOrderItemRepo;
import com.dbizz.model.OrderItem;
import com.dbizz.repo.OrderItemRepo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySqlOrderItemTest extends AbstractMySQLBase {

    private static DataSource dataSource;
    private static OrderItemRepo orderItemDAO;
    private static OrderItem orderItem = new OrderItem(1, 1, 1, 5, 10, null);

    @BeforeAll
    public static void setup() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl());
        config.setUsername(getUsername());
        config.setPassword(getPassword());
        config.setLeakDetectionThreshold(2000);

        dataSource = new HikariDataSource(config);
        orderItemDAO = new MySqlOrderItemRepo(dataSource);
        // product = new Product(1, "Fried Eggs", "Best fried eggs", 5, 20, null);
        System.out.println("Running ProductDAOmysql Test....");
    }

    @BeforeEach
    public void createTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS order_items (
                        id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                        orderId     BIGINT UNSIGNED NOT NULL,
                        productId   BIGINT UNSIGNED NOT NULL,
                        unitPrice   DECIMAL(12,2) NOT NULL CHECK (unitPrice >= 0),
                        quantity    INT UNSIGNED NOT NULL DEFAULT 1 CHECK (quantity >= 1),
                        subtotal    DECIMAL(12,2) GENERATED ALWAYS AS (quantity * unitPrice) STORED,
                        createdAt   DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),

                        INDEX idx_order_id (orderId),
                        INDEX idx_product_id (productId),
                        INDEX idx_order_items_desc (orderId, id DESC)

                      ) ENGINE=InnoDB
                        DEFAULT CHARSET=utf8mb4
                        COLLATE=utf8mb4_0900_ai_ci;
                                          """);
        }
    }

    @AfterEach
    public void truncateTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();) {
            st.execute("TRUNCATE TABLE order_items");
        }
    }

    @Test
    public void createOrderItem_whenOrderItemIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(null);
        });
    }

    // @Test
    // public void createOrderItem_whenOrderItemIdIsInvalid_throwsException() {
    // OrderItem invalidOrderItem = new OrderItem(0, 1, 1, 5, 10, null);
    // assertThrows(Exception.class, () -> {
    // orderItemDAO.create(invalidOrderItem);
    // });

    // OrderItem invalidOrderItem2 = new OrderItem(-2, 1, 1, 5, 10, null);
    // assertThrows(Exception.class, () -> {
    // orderItemDAO.create(invalidOrderItem2);
    // });
    // }

    @Test
    public void createOrderItem_whenOrderIdIsInvalid_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(1, 0, 1, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(invalidOrderItem);
        });

        OrderItem invalidOrderItem2 = new OrderItem(1, -2, 1, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(invalidOrderItem2);
        });
    }

    @Test
    public void createOrderItem_whenProductIdIsInvalid_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(1, 1, 0, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(invalidOrderItem);
        });

        OrderItem invalidOrderItem2 = new OrderItem(1, 1, -2, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(invalidOrderItem2);
        });
    }

    @Test
    public void createOrderItem_whenUnitPriceIsNegative_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(1, 1, 1, -5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(invalidOrderItem);
        });
    }

    @Test
    public void createOrderItem_whenQuantityIsNotPositive_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(1, 1, 1, 5, 0, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(invalidOrderItem);
        });

        OrderItem invalidOrderItem2 = new OrderItem(1, 1, 1, 5, -2, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.create(invalidOrderItem2);
        });
    }

    @Test
    public void createOrderItem_whenOrderItemIsValid_returnsGeneratedId() throws Exception {
        int generatedId = orderItemDAO.create(orderItem);
        assertThat(generatedId).isGreaterThan(0);
    }

    @Test
    public void findById_whenIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            orderItemDAO.findById(0);
        });

        assertThrows(Exception.class, () -> {
            orderItemDAO.findById(-2);
        });
    }

    @Test
    public void findById_whenIdDoesNotExist_returnsNull() throws Exception {
        OrderItem foundOrderItem = assertDoesNotThrow(() -> orderItemDAO.findById(9999));
        assertThat(foundOrderItem).isNull();
    }

    @Test
    public void findById_whenIdExists_returnsOrderItem() throws Exception {
        int generatedId = orderItemDAO.create(orderItem);
        assertThat(generatedId).isGreaterThan(0);
        OrderItem foundOrderItem = assertDoesNotThrow(() -> orderItemDAO.findById(generatedId));
        assertThat(foundOrderItem).isNotNull();
        assertThat(foundOrderItem.id()).isEqualTo(generatedId);
        assertThat(foundOrderItem.orderId()).isEqualTo(orderItem.orderId());
        assertThat(foundOrderItem.productId()).isEqualTo(orderItem.productId());
        assertThat(foundOrderItem.unitPrice()).isEqualTo(orderItem.unitPrice());
        assertThat(foundOrderItem.quantity()).isEqualTo(orderItem.quantity());
    }

    @Test
    public void findByOrderId_whenOrderIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            orderItemDAO.findByOrderId(0);
        });

        assertThrows(Exception.class, () -> {
            orderItemDAO.findByOrderId(-2);
        });
    }

    @Test
    public void findByOrderId_whenOrderIdDoesNotExist_returnsEmptyList() throws Exception {
        List<OrderItem> foundOrderItems = assertDoesNotThrow(() -> orderItemDAO.findByOrderId(9999));
        assertThat(foundOrderItems).isEmpty();
    }

    @Test
    public void findByOrderId_whenOrderIdExists_returnsOrderItemList() throws Exception {
        int generatedId = orderItemDAO.create(orderItem);
        assertThat(generatedId).isGreaterThan(0);
        OrderItem extraOrderItem = new OrderItem(2, orderItem.orderId(), 2, 10, 5, null);
        int extraGeneratedId = orderItemDAO.create(extraOrderItem);
        assertThat(extraGeneratedId).isGreaterThan(0);
        List<OrderItem> foundOrderItems = assertDoesNotThrow(() -> orderItemDAO.findByOrderId(orderItem.orderId()));
        assertThat(foundOrderItems).hasSize(2);
        Predicate<OrderItem> containsOrderItem1 = oi -> oi.id() == generatedId
                && oi.orderId() == orderItem.orderId()
                && oi.productId() == orderItem.productId()
                && oi.unitPrice() == orderItem.unitPrice()
                && oi.quantity() == orderItem.quantity();

        Predicate<OrderItem> containsOrderItem2 = oi -> oi.id() == extraGeneratedId
                && oi.orderId() == extraOrderItem.orderId()
                && oi.productId() == extraOrderItem.productId()
                && oi.unitPrice() == extraOrderItem.unitPrice()
                && oi.quantity() == extraOrderItem.quantity();

        assertThat(foundOrderItems).anyMatch(containsOrderItem1);
        assertThat(foundOrderItems).anyMatch(containsOrderItem2);
    }

    @Test
    public void findByProductId_whenProductIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            orderItemDAO.findByProductId(0);
        });

        assertThrows(Exception.class, () -> {
            orderItemDAO.findByProductId(-2);
        });
    }

    @Test
    public void findByProductId_whenProductIdDoesNotExist_returnsEmptyList() throws Exception {
        List<OrderItem> foundOrderItems = assertDoesNotThrow(() -> orderItemDAO.findByProductId(9999));
        assertThat(foundOrderItems).isEmpty();
    }

    @Test
    public void findByProductId_whenProductIdExists_returnsOrderItemList() throws Exception {
        int generatedId = orderItemDAO.create(orderItem);
        assertThat(generatedId).isGreaterThan(0);
        OrderItem extraOrderItem = new OrderItem(2, 2, orderItem.productId(), 10, 5, null);
        int extraGeneratedId = orderItemDAO.create(extraOrderItem);
        assertThat(extraGeneratedId).isGreaterThan(0);
        List<OrderItem> foundOrderItems = assertDoesNotThrow(() -> orderItemDAO.findByProductId(orderItem.productId()));
        assertThat(foundOrderItems).hasSize(2);
        Predicate<OrderItem> containsOrderItem1 = oi -> oi.id() == generatedId
                && oi.orderId() == orderItem.orderId()
                && oi.productId() == orderItem.productId()
                && oi.unitPrice() == orderItem.unitPrice()
                && oi.quantity() == orderItem.quantity();

        Predicate<OrderItem> containsOrderItem2 = oi -> oi.id() == extraGeneratedId
                && oi.orderId() == extraOrderItem.orderId()
                && oi.productId() == extraOrderItem.productId()
                && oi.unitPrice() == extraOrderItem.unitPrice()
                && oi.quantity() == extraOrderItem.quantity();

        assertThat(foundOrderItems).anyMatch(containsOrderItem1);
        assertThat(foundOrderItems).anyMatch(containsOrderItem2);
    }

    @Test
    public void findAll_whenNoOrderItemsExist_returnsEmptyList() throws Exception {
        List<OrderItem> foundOrderItems = assertDoesNotThrow(() -> orderItemDAO.findAll());
        assertThat(foundOrderItems).isEmpty();
    }

    @Test
    public void findAll_whenOrderItemsExist_returnsOrderItemList() throws Exception {
        int generatedId = orderItemDAO.create(orderItem);
        assertThat(generatedId).isGreaterThan(0);
        OrderItem extraOrderItem = new OrderItem(2, 2, 2, 10, 5, null);
        int extraGeneratedId = orderItemDAO.create(extraOrderItem);
        assertThat(extraGeneratedId).isGreaterThan(0);
        List<OrderItem> foundOrderItems = assertDoesNotThrow(() -> orderItemDAO.findAll());
        assertThat(foundOrderItems).hasSize(2);
        Predicate<OrderItem> containsOrderItem1 = oi -> oi.id() == generatedId
                && oi.orderId() == orderItem.orderId()
                && oi.productId() == orderItem.productId()
                && oi.unitPrice() == orderItem.unitPrice()
                && oi.quantity() == orderItem.quantity();

        Predicate<OrderItem> containsOrderItem2 = oi -> oi.id() == extraGeneratedId
                && oi.orderId() == extraOrderItem.orderId()
                && oi.productId() == extraOrderItem.productId()
                && oi.unitPrice() == extraOrderItem.unitPrice()
                && oi.quantity() == extraOrderItem.quantity();

        assertThat(foundOrderItems).anyMatch(containsOrderItem1);
        assertThat(foundOrderItems).anyMatch(containsOrderItem2);
    }

    @Test
    public void updateOrderItem_whenOrderItemIsNull_throwsException() {
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(null);
        });
    }

    @Test
    public void updateOrderItem_whenOrderItemIdIsInvalid_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(0, 1, 1, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem);
        });

        OrderItem invalidOrderItem2 = new OrderItem(-2, 1, 1, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem2);
        });
    }

    @Test
    public void updateOrderItem_whenOrderIdIsInvalid_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(1, 0, 1, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem);
        });

        OrderItem invalidOrderItem2 = new OrderItem(1, -2, 1, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem2);
        });
    }

    @Test
    public void updateOrderItem_whenProductIdIsInvalid_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(1, 1, 0, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem);
        });

        OrderItem invalidOrderItem2 = new OrderItem(1, 1, -2, 5, 10, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem2);
        });
    }

    @Test
    public void updateOrderItem_whenQuantityIsNotPositive_throwsException() {
        OrderItem invalidOrderItem = new OrderItem(1, 1, 1, 5, 0, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem);
        });

        OrderItem invalidOrderItem2 = new OrderItem(1, 1, 1, 5, -2, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem2);
        });
    }

    @Test
    public void updateOrderItem_whenUnitPriceIsNegative_throwsException() {
        OrderItem invalidOrderItem2 = new OrderItem(1, 1, 1, -2, 5, null);
        assertThrows(Exception.class, () -> {
            orderItemDAO.update(invalidOrderItem2);
        });
    }

    @Test
    public void updateOrderItem_whenOrderItemDoesNotExist_returnsZero() throws Exception {
        int updatedId = orderItemDAO.update(orderItem);
        assertThat(updatedId).isEqualTo(0);
    }

    @Test
    public void updateOrderItem_whenOrderItemExists_returnsOrderItemId() throws Exception {
        int generatedId = orderItemDAO.create(orderItem);
        assertThat(generatedId).isGreaterThan(0);
        OrderItem updatedOrderItem = new OrderItem(generatedId, 2, 2, 10, 5, null);
        int updatedId = orderItemDAO.update(updatedOrderItem);
        assertThat(updatedId).isEqualTo(generatedId);
        OrderItem foundOrderItem = assertDoesNotThrow(() -> orderItemDAO.findById(generatedId));
        assertThat(foundOrderItem).isNotNull();
        assertThat(foundOrderItem.id()).isEqualTo(generatedId);
        assertThat(foundOrderItem.orderId()).isEqualTo(updatedOrderItem.orderId());
        assertThat(foundOrderItem.productId()).isEqualTo(updatedOrderItem.productId());
        assertThat(foundOrderItem.unitPrice()).isEqualTo(updatedOrderItem.unitPrice());
        assertThat(foundOrderItem.quantity()).isEqualTo(updatedOrderItem.quantity());
    }

    @Test
    public void deleteOrderItem_whenIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            orderItemDAO.delete(0);
        });

        assertThrows(Exception.class, () -> {
            orderItemDAO.delete(-2);
        });
    }

    @Test
    public void deleteOrderItem_whenIdDoesNotExist_returnsZero() throws Exception {
        int deletedId = orderItemDAO.delete(9999);
        assertThat(deletedId).isEqualTo(0);
    }

    @Test
    public void deleteOrderItem_whenIdExists_returnsDeletedId() throws Exception {
        int generatedId = orderItemDAO.create(orderItem);
        assertThat(generatedId).isGreaterThan(0);
        int deletedId = orderItemDAO.delete(generatedId);
        assertThat(deletedId).isEqualTo(generatedId);
        OrderItem foundOrderItem = assertDoesNotThrow(() -> orderItemDAO.findById(generatedId));
        assertThat(foundOrderItem).isNull();
    }

}
