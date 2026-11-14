package com.uniajc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.DatabaseConexion;
import com.uniajc.modelo.Grupo;

/**
 * Acceso a datos de Grupo. Tabla en la BD: "practica-mvc".grupo
 * Solo ejecuta SQL; las validaciones están en GrupoService.
 */
public class GrupoDao {

    private static final String TABLA = "\"practica-mvc\".grupo";
    private static final String COLUMNAS = "id_grupo, id_materia, id_docente, aula, horario";

    private Connection conexion() {
        Connection conexion = DatabaseConexion.getConnection();
        if (conexion == null) {
            throw new RuntimeException("No hay conexión a la base de datos. Revise config.properties.");
        }
        return conexion;
    }

    public void guardar(Grupo grupo) {
        String sql = "INSERT INTO " + TABLA + " (id_materia, id_docente, aula, horario) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, grupo.getIdMateria());
            ps.setInt(2, grupo.getIdDocente());
            ps.setString(3, grupo.getAula());
            ps.setString(4, grupo.getHorario());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el grupo: " + e.getMessage(), e);
        }
    }

    public List<Grupo> obtenerTodos() {
        List<Grupo> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY id_grupo";
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los grupos: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Grupo> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE id_grupo = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el grupo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Busca si ya hay un grupo en la misma aula y el mismo horario
     * (sirve para evitar cruces de salón).
     */
    public Optional<Grupo> buscarPorAulaYHorario(String aula, String horario) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA
                + " WHERE LOWER(aula) = LOWER(?) AND LOWER(horario) = LOWER(?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, aula);
            ps.setString(2, horario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el grupo: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void actualizar(Grupo grupo) {
        String sql = "UPDATE " + TABLA
                + " SET id_materia = ?, id_docente = ?, aula = ?, horario = ? WHERE id_grupo = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, grupo.getIdMateria());
            ps.setInt(2, grupo.getIdDocente());
            ps.setString(3, grupo.getAula());
            ps.setString(4, grupo.getHorario());
            ps.setInt(5, grupo.getIdGrupo());
            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("Grupo no encontrado con ID: " + grupo.getIdGrupo());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el grupo: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id_grupo = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("Grupo no encontrado con ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el grupo: " + e.getMessage(), e);
        }
    }

    /** Convierte una fila de la tabla en un objeto Grupo. */
    private Grupo mapear(ResultSet rs) throws SQLException {
        return new Grupo(
            rs.getInt("id_grupo"),
            rs.getInt("id_materia"),
            rs.getInt("id_docente"),
            rs.getString("aula"),
            rs.getString("horario"));
    }
}
