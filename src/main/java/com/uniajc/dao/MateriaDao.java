package com.uniajc.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.uniajc.db.DatabaseConexion;
import com.uniajc.modelo.Materia;

/**
 * Acceso a datos de Materia. Tabla en la BD: "practica-mvc".materias
 */
public class MateriaDao {

    private static final String TABLA = "\"practica-mvc\".materias";
    private static final String COLUMNAS = "id, nombre_materia, creditos";

    private Connection conexion() {
        Connection conexion = DatabaseConexion.getConnection();
        if (conexion == null) {
            throw new RuntimeException("No hay conexión a la base de datos. Revise config.properties.");
        }
        return conexion;
    }

    public void guardar(Materia materia) {
        String sql = "INSERT INTO " + TABLA + " (nombre_materia, creditos) VALUES (?, ?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la materia: " + e.getMessage(), e);
        }
    }

    public List<Materia> obtenerTodos() {
        List<Materia> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " ORDER BY id";
        try (PreparedStatement ps = conexion().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las materias: " + e.getMessage(), e);
        }
        return lista;
    }

    public Optional<Materia> buscarPorId(int id) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la materia: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Materia> buscarPorNombre(String nombre) {
        String sql = "SELECT " + COLUMNAS + " FROM " + TABLA + " WHERE LOWER(nombre_materia) = LOWER(?)";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la materia: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void actualizar(Materia materia) {
        String sql = "UPDATE " + TABLA + " SET nombre_materia = ?, creditos = ? WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setString(1, materia.getNombreMateria());
            ps.setInt(2, materia.getCreditos());
            ps.setInt(3, materia.getIdMateria());
            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("Materia no encontrada con ID: " + materia.getIdMateria());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la materia: " + e.getMessage(), e);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM " + TABLA + " WHERE id = ?";
        try (PreparedStatement ps = conexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new RuntimeException("Materia no encontrada con ID: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la materia: " + e.getMessage(), e);
        }
    }

    private Materia mapear(ResultSet rs) throws SQLException {
        return new Materia(
            rs.getInt("id"),
            rs.getString("nombre_materia"),
            rs.getInt("creditos"));
    }
}