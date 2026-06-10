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
import com.dbizz.database.mysql.MySqlProductRepo;
import com.dbizz.model.Product;
import com.dbizz.model.ProductCategory;
import com.dbizz.model.User;
import com.dbizz.repo.ProductRepo;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MySqlProductRepoTest extends AbstractMySQLBase {

    private static DataSource dataSource;
    private static ProductRepo productDAO;
    private static Product product = new Product(1, "Fried Eggs", ProductCategory.MAIN_COURSE, "Best fried eggs", 5, 20,
            null, null);

    @BeforeAll
    public static void setup() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getJdbcUrl());
        config.setUsername(getUsername());
        config.setPassword(getPassword());
        config.setLeakDetectionThreshold(2000);

        dataSource = new HikariDataSource(config);
        productDAO = new MySqlProductRepo(dataSource);
        // product = new Product(1, "Fried Eggs", "Best fried eggs", 5, 20, null);
        System.out.println("Running ProductDAOmysql Test....");
    }

    @BeforeEach
    public void createTable() throws SQLException {
        try (Connection conn = dataSource.getConnection();
                Statement st = conn.createStatement();) {
            st.execute("""
                    CREATE TABLE IF NOT EXISTS products (
                        id          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                        name        VARCHAR(255) NOT NULL,
                        category    VARCHAR(50) NOT NULL,
                        description TEXT NULL,
                        price       DECIMAL(12,2) NOT NULL CHECK (price >= 0),
                        stock       INT UNSIGNED NOT NULL DEFAULT 0 CHECK (stock >= 0),
                        image_url   VARCHAR(512) NULL,
                        createdAt   DATETIME NOT NULL DEFAULT (UTC_TIMESTAMP()),

                        INDEX idx_name (name)

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
            st.execute("TRUNCATE TABLE products");
        }
    }

    @Test
    public void createProduct_whenProductIsNull_throwsException() {
        Product nullProduct = null;
        assertThrows(Exception.class, () -> {
            productDAO.create(nullProduct);
        });

        // assertThat(ex.getMessage()).isEqualTo("Invalid ID");
    }

    @Test
    public void createProduct_whenProductNameIsNull_throwsException() {
        Product nullNameProduct = new Product(product.id(), null, product.category(), product.description(),
                product.price(),
                product.stock(), null, null);
        assertThrows(Exception.class, () -> {
            productDAO.create(nullNameProduct);
        });

        // assertThat(ex.getMessage()).isEqualTo("Invalid ID");
    }

    @Test
    public void createProduct_whenProductPriceIsNegative_throwsException() {
        Product negativePriceProduct = new Product(product.id(), product.name(), product.category(),
                product.description(), -10,
                product.stock(), null, null);
        assertThrows(Exception.class, () -> {
            productDAO.create(negativePriceProduct);
        });
    }

    @Test
    public void createProduct_whenProductStockIsNegative_throwsException() {
        Product negativeStockProduct = new Product(product.id(), product.name(), product.category(),
                product.description(), product.price(),
                -10, null, null);
        assertThrows(Exception.class, () -> {
            productDAO.create(negativeStockProduct);
        });
    }

    @Test
    public void createProduct_whenProductIsValid_doesNotThrowException_returnsNewProductId() {
        int id = assertDoesNotThrow(() -> {
            return productDAO.create(product);
        });
        assertTrue(id > 0);
    }

    @Test
    public void updateProduct_whenProductIdIsInvalid_throwsException() {
        Product invalidIdProduct = new Product(0, product.name(), product.category(), product.description(),
                product.price(),
                product.stock(), null, null);
        assertThrows(Exception.class, () -> {
            productDAO.update(invalidIdProduct);
        });
        Product invalidIdProduct2 = new Product(-2, product.name(), product.category(), product.description(),
                product.price(),
                product.stock(), null, null);
        assertThrows(Exception.class, () -> {
            productDAO.update(invalidIdProduct2);
        });
    }

    @Test
    public void updateProduct_whenProductNameIsNull_throwsException() {
        Product nullNameProduct = new Product(product.id(), null, product.category(), product.description(),
                product.price(),
                product.stock(), null, null);
        assertThrows(Exception.class, () -> {
            productDAO.update(nullNameProduct);
        });
    }

    @Test
    public void updateProduct_whenProductPriceIsNegative_throwsException() {
        Product negativePriceProduct = new Product(product.id(), product.name(), product.category(),
                product.description(), -10,
                product.stock(), null, null);
        assertThrows(Exception.class, () -> {
            productDAO.update(negativePriceProduct);
        });
    }

    @Test
    public void updateProduct_whenProductStockIsNegative_throwsException() {
        Product negativeStockProduct = new Product(product.id(), product.name(), product.category(),
                product.description(), product.price(),
                -10, null, null);
        assertThrows(Exception.class, () -> {
            productDAO.update(negativeStockProduct);
        });
    }

    @Test
    public void updateProduct_whenProductIdDoesNotExist_doesNotThrowException_returnsZeroId() {
        int id = assertDoesNotThrow(() -> {
            return productDAO.update(product);
        });
        assertEquals(0, id);
    }

    @Test
    public void updateProduct_whenProductIdExists_returnsUpdatedStockID_findById_returnsMatchedProduct() {
        int id = assertDoesNotThrow(() -> {
            return productDAO.create(product);
        });
        Product updateProduct = new Product(id, "UpdatedName", ProductCategory.MAIN_COURSE, "UpdatedDescription", 50,
                50, null);
        int updatedId = assertDoesNotThrow(() -> {
            return productDAO.update(updateProduct);
        });
        assertEquals(id, updatedId);

        Product matchedProduct = assertDoesNotThrow(() -> {
            return productDAO.findById(updatedId);
        });

        assertEquals(updateProduct.id(), matchedProduct.id());
        assertEquals(updateProduct.name(), matchedProduct.name());
        assertEquals(updateProduct.price(), matchedProduct.price());
        assertEquals(updateProduct.stock(), matchedProduct.stock());
    }

    @Test
    public void findById_whenProductIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            productDAO.findById(-2);
            productDAO.findById(0);
        });
    }

    @Test
    public void findById_whenProductIdDoesNotExist_returnsNull() {
        Product searchedProduct = assertDoesNotThrow(() -> {
            return productDAO.findById(product.id());
        });
        assertNull(searchedProduct);
    }

    @Test
    public void findById_whenProductIdExists_returnsMatchedProduct() {
        Product searchedProduct = assertDoesNotThrow(() -> {
            int id = productDAO.create(product);
            return productDAO.findById(id);
        });
        assertThat(searchedProduct.name()).isEqualTo(product.name());
        assertThat(searchedProduct.description()).isEqualTo(product.description());
        assertEquals(searchedProduct.price(), product.price(), () -> {
            return "Price not equal : " + searchedProduct.price() + " - " + product.price();
        });
        assertEquals(searchedProduct.stock(), product.stock());
    }

    @Test
    public void findByName_whenProductNameIsNull_throwsException() {
        assertThatThrownBy(() -> {
            productDAO.findByName(null);
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void findByName_whenProductNameDoesNotExist_returnsEmptyList() {
        List<Product> searchedProducts = assertDoesNotThrow(() -> {
            return productDAO.findByName(product.name());
        });
        assertThat(searchedProducts).isEmpty();
    }

    @Test
    public void findByName_whenProductNameExists_returnsMatchedProduct() {
        List<Product> searchedProducts = assertDoesNotThrow(() -> {
            productDAO.create(product);
            return productDAO.findByName(product.name());
        });
        assertThat(searchedProducts).isNotEmpty();
        Product searchedProduct = searchedProducts.get(0);
        assertThat(searchedProduct.name()).isEqualTo(product.name());
        assertThat(searchedProduct.description()).isEqualTo(product.description());
        assertEquals(searchedProduct.price(), product.price(), () -> {
            return "Price not equal : " + searchedProduct.price() + " - " + product.price();
        });
        assertEquals(searchedProduct.stock(), product.stock());

    }

    @Test
    public void findByName_whenMultipleProductsWithSimilarNameExist_returnsAllMatchedProducts() {
        Product similarProduct1 = new Product(2, "Fried Eggs Sandwich", product.category(), "Delicious sandwich", 7, 15,
                null, null);
        Product similarProduct2 = new Product(3, "Spicy Fried Eggs", product.category(), "Spicy and tasty", 6, 10,
                null, null);
        List<Product> searchedProducts = assertDoesNotThrow(() -> {
            productDAO.create(product);
            productDAO.create(similarProduct1);
            productDAO.create(similarProduct2);
            return productDAO.findByName("Fried Eggs");
        });
        assertThat(searchedProducts).hasSize(3);
    }

    @Test
    public void findByCategory_whenCategoryIsNull_throwsException() {
        assertThatThrownBy(() -> {
            productDAO.findByCategory(null);
        }).isInstanceOf(Exception.class);
    }

    @Test
    public void findByCategory_whenCategoryDoesNotExist_returnsEmptyList() {
        List<Product> products = assertDoesNotThrow(() -> {
            return productDAO.findByCategory(ProductCategory.MAIN_COURSE);
        });
        assertThat(products).isEmpty();
    }

    @Test
    public void findByCategory_whenCategoryExists_returnsMatchedProducts() {
        List<Product> products = assertDoesNotThrow(() -> {
            productDAO.create(product);
            return productDAO.findByCategory(product.category());
        });
        Predicate<Product> isProduct1 = s1 -> s1.name().equals(product.name()) &&
                s1.category().equals(product.category()) &&
                s1.description().equals(product.description()) &&
                s1.price() == product.price() &&
                s1.stock() == product.stock();
        assertThat(products).hasSize(1)
                .anyMatch(isProduct1);
    }

    @Test
    public void findAll_whenNoProductsExist_returnsEmptyList() {
        List<Product> products = assertDoesNotThrow(() -> {
            return productDAO.findAll();
        });
        assertThat(products).isEmpty();
    }

    @Test
    public void findAll_whenProductsExists_returnsAllProducts() {
        Product extraProduct = new Product(2, "extraName", ProductCategory.MAIN_COURSE, "extraDescription", 50, 50,
                null, null);
        List<Product> products = assertDoesNotThrow(() -> {
            productDAO.create(product);
            productDAO.create(extraProduct);
            return productDAO.findAll();
        });

        Predicate<Product> isProduct1 = s1 -> s1.name().equals(product.name()) &&
                s1.category().equals(product.category()) &&
                s1.description().equals(product.description()) &&
                s1.price() == product.price() &&
                s1.stock() == product.stock();

        Predicate<Product> isProduct2 = s2 -> s2.name().equals(extraProduct.name()) &&
                s2.category().equals(extraProduct.category()) &&
                s2.description().equals(extraProduct.description()) &&
                s2.price() == extraProduct.price() &&
                s2.stock() == extraProduct.stock();

        assertThat(products).hasSize(2)
                .anyMatch(isProduct1)
                .anyMatch(isProduct2);
    }

    @Test
    public void deleteProduct_whenIdIsInvalid_throwsException() {
        assertThrows(Exception.class, () -> {
            productDAO.delete(0);
        });
        assertThrows(Exception.class, () -> {
            productDAO.delete(-2);
        });
    }

    @Test
    public void deleteUser_whenIdDoesNotExist_returnsZeroId() {
        int id = assertDoesNotThrow(() -> {
            return productDAO.delete(1);
        });
        assertEquals(0, id);
    }

    @Test
    public void deleteUser_whenIdExists_returnsDeletedId_findById_returnsNull() {
        int id = assertDoesNotThrow(() -> {
            return productDAO.create(product);
        });
        int deletedId = assertDoesNotThrow(() -> {
            return productDAO.delete(id);
        });
        assertEquals(id, deletedId);
        Product deletedProduct = assertDoesNotThrow(() -> {
            return productDAO.findById(deletedId);
        });
        assertNull(deletedProduct);
    }

}
