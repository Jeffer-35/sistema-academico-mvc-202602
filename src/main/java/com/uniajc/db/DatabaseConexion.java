package com.uniajc.db;

// Clases de JDBC para conectarse a la base de datos
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Clases para leer el archivo config.properties
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Clase que maneja la conexión con la base de datos PostgreSQL.
 * Usa una sola conexión para todo el programa (singleton).
 * Los datos de conexión se leen del archivo config.properties.
 */
public class DatabaseConexion {

    // Conexión única que comparte todo el sistema
    private static Connection connection = null;

    /**
     * Obtiene la conexión a la base de datos.
     * Si no existe o se cerró, la crea de nuevo.
     *
     * @return Objeto Connection listo para usar, o null si falla.
     */
    public static Connection getConnection() {

        // 1. Revisar si la conexión existe pero ya se cerró
        try {
            if (connection != null && connection.isClosed()) {
                connection = null;
            }
        } catch (SQLException e) {
            connection = null;
        }

        // 2. Si no hay conexión, se crea una nueva
        if (connection == null) {

            Properties properties = new Properties();

            // try-with-resources: el archivo se cierra solo al terminar de leerlo
            try (FileInputStream archivo = new FileInputStream("config.properties")) {

                // Cargar los datos del archivo
                properties.load(archivo);

                // Leer los parámetros de conexión
                String url = properties.getProperty("URL");
                String user = properties.getProperty("USER");
                String password = properties.getProperty("PASSWORD");

                // Se muestra solo la URL y el usuario, NUNCA la contraseña
                System.out.println("URL =>>> " + url);
                System.out.println("User =>>> " + user);

                // Establecer la conexión
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Conexion exitosa.");

            } catch (SQLException error) {
                System.out.println("No se pudo conectar a la base de datos. " + error.getMessage());
            } catch (FileNotFoundException e) {
                System.out.println("No se encontró el archivo config.properties.");
            } catch (IOException e) {
                System.out.println("Error al leer el archivo config.properties.");
            }
        }
        return connection;
    }

    /**
     * Cierra la conexión a la base de datos si está abierta.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null; // Se deja en null para poder conectar otra vez si se necesita
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.out.println("No se pudo cerrar la conexión. " + e.getMessage());
            }
        }
    }
}