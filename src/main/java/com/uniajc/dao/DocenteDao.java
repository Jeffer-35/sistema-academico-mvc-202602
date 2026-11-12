package com.uniajc.dao;

// Importaciones necesarias para manejar las consultas SQL
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Importar la clase de conexión y el modelo
import com.uniajc.db.DatabaseConexion;
import com.uniajc.modelo.Docente;

/**
 * Clase DAO (Data Access Object) para la entidad Docente.
 * Contiene todas las operaciones CRUD con la base de datos.
 *
 * Tabla en la BD: "practica-mvc".docente
 * Columnas en la BD: id_docente, nombre, especialidad
 * (en el modelo Docente se llaman: idDocente, nombre, especialidad)
 *
 * IMPORTANTE: DatabaseConexion maneja una conexión única (singleton),
 * por eso aquí NO cerramos la Connection; solo el PreparedStatement y el ResultSet.
 */
public class DocenteDao {

    /**
     * Obtiene la conexión compartida y valida que exista.
     */
    private Connection obtenerConexion() {
        Connection conn = DatabaseConexion.getConnection();
        if (conn == null) {
            throw new RuntimeException("No hay conexión a la base de datos. Revise config.properties.");
        }
        return conn;
    }

    /**
     * Guarda un nuevo docente. El id lo genera la base de datos.
     * @param docente Docente a guardar
     */
    public void guardar(Docente docente) {
        String sql = "INSERT INTO \"practica-mvc\".docente (nombre, especialidad) VALUES (?, ?)";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, docente.getNombre());
            pstmt.setString(2, docente.getEspecialidad());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el docente: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los docentes ordenados por id.
     * @return Lista de docentes
     */
    public List<Docente> obtenerTodos() {
        List<Docente> lista = new ArrayList<>();
        String sql = "SELECT id_docente, nombre, especialidad "
                + "FROM \"practica-mvc\".docente ORDER BY id_docente";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearDocente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener los docentes: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Busca un docente por su id.
     * @param id Identificador del docente
     * @return Optional con el docente si existe, vacío si no
     */
    public Optional<Docente> buscarPorId(int id) {
        String sql = "SELECT id_docente, nombre, especialidad "
                + "FROM \"practica-mvc\".docente WHERE id_docente = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearDocente(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el docente: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Actualiza los datos de un docente existente.
     * @param docente Docente con los datos nuevos
     */
    public void actualizar(Docente docente) {
        String sql = "UPDATE \"practica-mvc\".docente SET nombre = ?, especialidad = ? WHERE id_docente = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, docente.getNombre());
            pstmt.setString(2, docente.getEspecialidad());
            pstmt.setInt(3, docente.getIdDocente());

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se encontró docente con ID: " + docente.getIdDocente());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el docente: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un docente por su id.
     * Si el docente tiene grupos asignados, la base de datos no deja borrarlo
     * (clave foránea de la tabla grupo).
     * @param id Identificador del docente
     */
    public void eliminar(int id) {
        String sql = "DELETE FROM \"practica-mvc\".docente WHERE id_docente = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se encontró docente con ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el docente: " + e.getMessage(), e);
        }
    }

    /**
     * Convierte una fila del ResultSet en un objeto Docente.
     * Aquí se "traducen" las columnas de la BD a los atributos del modelo.
     */
    private Docente mapearDocente(ResultSet rs) throws SQLException {
        Docente docente = new Docente();
        docente.setIdDocente(rs.getInt("id_docente"));
        docente.setNombre(rs.getString("nombre"));
        docente.setEspecialidad(rs.getString("especialidad"));
        return docente;
    }
}