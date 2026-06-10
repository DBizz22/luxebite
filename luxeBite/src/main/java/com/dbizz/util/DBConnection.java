package com.dbizz.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;

public class DBConnection {

    private DBConnection() {
    }

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    private static final Properties props = loadProperties();

    private static DataSource dataSource = initMySQLDatabase();

    private static Properties loadProperties() {
        Properties p = new Properties();
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read db.properties", e);
        }
        return p;
    }

    private static String resolve(String envKey, String propKey, String defaultValue) {
        // Order: real OS environment variable -> .env file -> db.properties -> default
        String v = System.getenv(envKey);
        if (v == null || v.isBlank()) {
            v = dotenv.get(envKey);
        }
        if (v == null || v.isBlank()) {
            v = props.getProperty(propKey);
        }
        if (v == null || v.isBlank()) {
            v = defaultValue;
        }
        return v;
    }

    private static String required(String envKey, String propKey) {
        String v = resolve(envKey, propKey, null);
        if (v == null || v.isBlank()) {
            throw new IllegalStateException(
                    "Database credential not configured: set the " + envKey
                            + " environment variable or the " + propKey + " property in db.properties");
        }
        return v;
    }

    private static DataSource initMySQLDatabase() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(resolve("DB_DRIVER", "db.driver", "com.mysql.cj.jdbc.Driver"));
        config.setJdbcUrl(resolve("DB_URL", "db.url", "jdbc:mysql://localhost:3306/luxebite_db"));
        config.setUsername(required("DB_USER", "db.user"));
        config.setPassword(required("DB_PASSWORD", "db.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        DataSource dataSource = new HikariDataSource(config);
        return dataSource;
    }

    public static DataSource getMySQLDataSource() {
        return dataSource;
    }

}
