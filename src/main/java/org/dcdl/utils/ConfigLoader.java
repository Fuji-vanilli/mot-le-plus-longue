package org.dcdl.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {

    public static Properties getProperties() {
        Properties properties= new Properties();
        try (FileInputStream fileInputStream= new FileInputStream("src/main/resources/application.properties");) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException("error to load the file", e);
        }

        return properties;
    }

}
