package com.dbizz.database;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.mysql.MySQLContainer;

@Testcontainers
public abstract class AbstractMySQLBase {

    @Container
    public static final MySQLContainer mysqlContainer = new MySQLContainer("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    public static void startContainer() {
        System.out.println("Starting MySQL Test Container...");
        // mysqlContainer.start();
    }

    protected static String getJdbcUrl() {
        return mysqlContainer.getJdbcUrl();
    }

    protected static String getUsername() {
        return mysqlContainer.getUsername();
    }

    protected static String getPassword() {
        return mysqlContainer.getPassword();
    }

}
