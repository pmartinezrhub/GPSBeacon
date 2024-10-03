package com.example.gpsbeacon;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    public static void main(String[] args) {
        Properties properties = new Properties();

        try (FileInputStream input = new FileInputStream("config.properties")) {
            // Cargar el archivo de propiedades
            properties.load(input);

            // Acceder a las propiedades
            String url = properties.getProperty("database.url");
            String username = properties.getProperty("database.username");
            String password = properties.getProperty("database.password");

            System.out.println("Database URL: " + url);
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
