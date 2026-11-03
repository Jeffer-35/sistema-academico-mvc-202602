package com.uniajc.dao;

// Importaciones necesarias para manejar la conexión y las consultas SQL
import java.sql.Connection;        // Para establecer la conexión con la base de datos
import java.sql.PreparedStatement; // Para preparar consultas SQL seguras
import java.sql.ResultSet;         // Para recibir los resultados de las consultas
import java.sql.SQLException;      // Para manejar errores de SQL
import java.util.ArrayList;        // Para crear listas dinámicas
import java.util.List;             // Interfaz de lista
import java.util.Optional;         // Para manejar valores que pueden ser nulos

// Importar la clase de conexión y el modelo
import com.uniajc.db.DatabaseConexion;
import com.uniajc.modelo.Estudiante;

/**
 * Clase DAO (Data Access Object) para la entidad Estudiante.
 * Contiene todas las operaciones CRUD con la base de datos.
 *
 * Tabla en la BD: "practica-mvc".estudiantes
 * Columnas en la BD: id, name, lastname, email
 * (en el modelo Estudiante se llaman: idEstudiante, nombre, apellido, email)
 *
 * IMPORTANTE: DatabaseConexion maneja una conexión única (singleton),
 * por eso aquí NO cerramos la Connection; solo cerramos el
 * PreparedStatement y el ResultSet de cada consulta.
 */
public class EstudianteDao {

    /**
     * Obtiene la conexión compartida y valida que exista.
     * Si config.properties no se encontró, getConnection() devuelve null.
     */
    private Connection obtenerConexion() {
        Connection conn = DatabaseConexion.getConnection();
        if (conn == null) {
            throw new RuntimeException("No hay conexión a la base de datos. Revise config.properties.");
        }
        return conn;
    }

    /**
     * Guarda un nuevo estudiante en la base de datos.
     * El id lo genera la base de datos, por eso no se envía en el INSERT.
     * @param estudiante Objeto Estudiante a guardar
     */
    public void guardar(Estudiante estudiante) {
        // Consulta SQL para insertar un nuevo estudiante
        String sql = "INSERT INTO \"practica-mvc\".estudiantes (name, lastname, email) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {

            // Asignar los valores a los parámetros de la consulta
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());

            // Ejecutar la consulta
            pstmt.executeUpdate();
            System.out.println("Estudiante guardado correctamente.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los estudiantes de la base de datos.
     * @return Lista de todos los estudiantes
     */
    public List<Estudiante> obtenerTodos() {
        // Lista donde se almacenarán los estudiantes obtenidos
        List<Estudiante> lista = new ArrayList<>();

        // Consulta SQL para obtener todos los estudiantes
        String sql = "SELECT id, name, lastname, email FROM \"practica-mvc\".estudiantes ORDER BY id";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Recorrer los resultados y crear objetos Estudiante
            while (rs.next()) {
                lista.add(mapearEstudiante(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los estudiantes: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Busca un estudiante por su ID.
     * @param id Identificador del estudiante
     * @return Optional con el estudiante si existe, vacío si no
     */
    public Optional<Estudiante> buscarPorId(int id) {
        // Consulta SQL para buscar por ID
        String sql = "SELECT id, name, lastname, email FROM \"practica-mvc\".estudiantes WHERE id = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {

            // Asignar el ID como parámetro
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Si encontró un resultado, retornar el estudiante
                if (rs.next()) {
                    return Optional.of(mapearEstudiante(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el estudiante: " + e.getMessage(), e);
        }
        // Si no encontró nada, retornar Optional vacío
        return Optional.empty();
    }

    /**
     * Actualiza los datos de un estudiante existente.
     * @param estudiante Objeto Estudiante con los datos actualizados
     */
    public void actualizar(Estudiante estudiante) {
        // Consulta SQL para actualizar un estudiante
        String sql = "UPDATE \"practica-mvc\".estudiantes SET name = ?, lastname = ?, email = ? WHERE id = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {

            // Asignar los nuevos valores
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.setInt(4, estudiante.getIdEstudiante());

            // Ejecutar y verificar que se actualizó al menos una fila
            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se encontró estudiante con ID: " + estudiante.getIdEstudiante());
            }
            System.out.println("Estudiante actualizado correctamente.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un estudiante de la base de datos por su ID.
     * @param id Identificador del estudiante a eliminar
     */
    public void eliminar(int id) {
        // Consulta SQL para eliminar un estudiante
        String sql = "DELETE FROM \"practica-mvc\".estudiantes WHERE id = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {

            // Asignar el ID como parámetro
            pstmt.setInt(1, id);

            // Ejecutar y verificar que se eliminó al menos una fila
            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se encontró estudiante con ID: " + id);
            }
            System.out.println("Estudiante eliminado correctamente.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el estudiante: " + e.getMessage(), e);
        }
    }

    /**
     * Método privado que convierte un ResultSet en un objeto Estudiante.
     * @param rs ResultSet con los datos del estudiante
     * @return Objeto Estudiante con los datos mapeados
     */
    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante();
        // Mapear cada columna de la BD al atributo correspondiente del modelo
        estudiante.setIdEstudiante(rs.getInt("id"));
        estudiante.setNombre(rs.getString("name"));
        estudiante.setApellido(rs.getString("lastname"));
        estudiante.setEmail(rs.getString("email"));
        return estudiante;
    }
}