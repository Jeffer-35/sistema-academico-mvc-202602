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
 * Tabla en la BD: "practica-mvc".estudiante
 * Columnas en la BD: id_estudiante, nombre, apellido, email
 * (en el modelo Estudiante se llaman: idEstudiante, nombre, apellido, email)
 *
 * Se usa la tabla "estudiante" (singular) porque inscripcion_curso
 * tiene una clave foránea hacia ella.
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
        String sql = "INSERT INTO \"practica-mvc\".estudiante (nombre, apellido, email) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());

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
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT id_estudiante, nombre, apellido, email "
                + "FROM \"practica-mvc\".estudiante ORDER BY id_estudiante";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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
        String sql = "SELECT id_estudiante, nombre, apellido, email "
                + "FROM \"practica-mvc\".estudiante WHERE id_estudiante = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearEstudiante(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el estudiante: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Actualiza los datos de un estudiante existente.
     * @param estudiante Objeto Estudiante con los datos actualizados
     */
    public void actualizar(Estudiante estudiante) {
        String sql = "UPDATE \"practica-mvc\".estudiante "
                + "SET nombre = ?, apellido = ?, email = ? WHERE id_estudiante = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, estudiante.getNombre());
            pstmt.setString(2, estudiante.getApellido());
            pstmt.setString(3, estudiante.getEmail());
            pstmt.setInt(4, estudiante.getIdEstudiante());

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
     * Si el estudiante tiene inscripciones, la base de datos no deja borrarlo
     * (clave foránea de inscripcion_curso).
     * @param id Identificador del estudiante a eliminar
     */
    public void eliminar(int id) {
        String sql = "DELETE FROM \"practica-mvc\".estudiante WHERE id_estudiante = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);

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
     * Convierte una fila del ResultSet en un objeto Estudiante.
     * Aquí se "traducen" las columnas de la BD a los atributos del modelo.
     */
    private Estudiante mapearEstudiante(ResultSet rs) throws SQLException {
        Estudiante estudiante = new Estudiante();
        estudiante.setIdEstudiante(rs.getInt("id_estudiante"));
        estudiante.setNombre(rs.getString("nombre"));
        estudiante.setApellido(rs.getString("apellido"));
        estudiante.setEmail(rs.getString("email"));
        return estudiante;
    }
}