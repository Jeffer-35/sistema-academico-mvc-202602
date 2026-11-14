package com.uniajc.dao;

// Importaciones necesarias para manejar las consultas SQL
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Importar la clase de conexión y el modelo
import com.uniajc.db.DatabaseConexion;
import com.uniajc.modelo.Inscripcion;

/**
 * Clase DAO (Data Access Object) para la entidad Inscripcion.
 * Contiene todas las operaciones CRUD con la base de datos.
 *
 * Tabla en la BD: "practica-mvc".inscripcion_curso
 * Columnas en la BD: id_inscripcion, id_estudiante, id_grupo, nota_final, estado
 *
 * IMPORTANTE: DatabaseConexion maneja una conexión única (singleton),
 * por eso aquí NO cerramos la Connection; solo el PreparedStatement y el ResultSet.
 */
public class InscripcionDao {

    // Columnas que se consultan en todos los SELECT
    private static final String COLUMNAS = "id_inscripcion, id_estudiante, id_grupo, nota_final, estado";
    private static final String TABLA = "\"practica-mvc\".inscripcion_curso";

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
     * Guarda una nueva inscripción. El id lo genera la base de datos.
     * @param inscripcion Inscripción a guardar
     */
    public void guardar(Inscripcion inscripcion) {
        String sql = "INSERT INTO " + TABLA + " (id_estudiante, id_grupo, nota_final, estado) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, inscripcion.getIdEstudiante());
            pstmt.setInt(2, inscripcion.getIdGrupo());
            asignarNota(pstmt, 3, inscripcion.getNotaFinal());
            pstmt.setString(4, inscripcion.getEstado());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la inscripción: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las inscripciones ordenadas por id.
     * @return Lista de inscripciones
     */
    public List<Inscripcion> obtenerTodos() {
        List<Inscripcion> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY id_inscripcion";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearInscripcion(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las inscripciones: " + e.getMessage(), e);
        }
        return lista;
    }

    /**
     * Busca una inscripción por su id.
     * @param id Identificador de la inscripción
     * @return Optional con la inscripción si existe, vacío si no
     */
    public Optional<Inscripcion> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE id_inscripcion = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearInscripcion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la inscripción: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Busca si un estudiante ya está inscrito en un grupo.
     * La usa el servicio para no permitir inscripciones repetidas.
     * @param idEstudiante Estudiante
     * @param idGrupo Grupo
     * @return Optional con la inscripción si ya existe
     */
    public Optional<Inscripcion> buscarPorEstudianteYGrupo(int idEstudiante, int idGrupo) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE id_estudiante = ? AND id_grupo = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, idEstudiante);
            pstmt.setInt(2, idGrupo);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearInscripcion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la inscripción: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Actualiza una inscripción existente.
     * @param inscripcion Inscripción con los datos nuevos
     */
    public void actualizar(Inscripcion inscripcion) {
        String sql = "UPDATE " + TABLA
                + " SET id_estudiante = ?, id_grupo = ?, nota_final = ?, estado = ? WHERE id_inscripcion = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, inscripcion.getIdEstudiante());
            pstmt.setInt(2, inscripcion.getIdGrupo());
            asignarNota(pstmt, 3, inscripcion.getNotaFinal());
            pstmt.setString(4, inscripcion.getEstado());
            pstmt.setInt(5, inscripcion.getIdInscripcion());

            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se encontró inscripción con ID: " + inscripcion.getIdInscripcion());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la inscripción: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una inscripción por su id.
     * @param id Identificador de la inscripción
     */
    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id_inscripcion = ?";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int filas = pstmt.executeUpdate();
            if (filas == 0) {
                throw new RuntimeException("No se encontró inscripción con ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la inscripción: " + e.getMessage(), e);
        }
    }

    /**
     * La nota puede estar vacía (cuando el estudiante apenas se inscribe).
     * Si es null se guarda NULL en la base de datos.
     */
    private void asignarNota(PreparedStatement pstmt, int posicion, Double nota) throws SQLException {
        if (nota == null) {
            pstmt.setNull(posicion, Types.DOUBLE);
        } else {
            pstmt.setDouble(posicion, nota);
        }
    }

    /**
     * Convierte una fila del ResultSet en un objeto Inscripcion.
     * Aquí se "traducen" las columnas de la BD a los atributos del modelo.
     */
    private Inscripcion mapearInscripcion(ResultSet rs) throws SQLException {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdInscripcion(rs.getInt("id_inscripcion"));
        inscripcion.setIdEstudiante(rs.getInt("id_estudiante"));
        inscripcion.setIdGrupo(rs.getInt("id_grupo"));

        // getDouble devuelve 0 si la columna está vacía; con wasNull() sabemos si era NULL
        double nota = rs.getDouble("nota_final");
        inscripcion.setNotaFinal(rs.wasNull() ? null : nota);

        inscripcion.setEstado(rs.getString("estado"));
        return inscripcion;
    }
}
