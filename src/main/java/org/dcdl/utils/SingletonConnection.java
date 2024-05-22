package org.dcdl.utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

public class SingletonConnection {
    private static Connection connection;
    private static final Properties properties;

    static {
        properties= ConfigLoader.getProperties();
    }

    private SingletonConnection() {

    }
    public static synchronized Connection getConnection() {
        if (!Objects.isNull(connection)) {
            return connection;
        }

        final String URL_DATABASE= properties.getProperty("database-url");
        final String USERNAME_DATABASE= properties.getProperty("database-username");
        final String PASSWORD_DATABASE= properties.getProperty("database-password");

        Connection connection;
        try {

            connection= DriverManager.getConnection(URL_DATABASE, USERNAME_DATABASE, PASSWORD_DATABASE);
        } catch (SQLException e) {
            throw new RuntimeException("error to connect into the database!",e);
        }

        return connection;
    }


}
