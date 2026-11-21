package com.uniajc.dao;

// Importaciones necesarias para manejar las consultas SQL
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

// Importar la clase de conexión y el modelo
import com.uniajc.db.DatabaseConexion;
import com.uniajc.modelo.Usuario;

/**
 * Clase DAO (Data Access Object) para la entidad Usuario.
 * Solo se necesita buscar por nombre de usuario para el login.
 *
 * Tabla en la BD: "practica-mvc".usuarios
 *
 * IMPORTANTE: se usa PreparedStatement (con ?) para evitar inyección SQL:
 * si alguien escribe algo como  ' OR '1'='1  en el usuario, se trata como texto
 * y no como parte de la consulta.
 */
public class UsuarioDao {

    private static final String TABLA = "\"practica-mvc\".usuarios";

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
     * Busca un usuario por su nombre de usuario (sin importar mayúsculas).
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe, vacío si no
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        String sql = "SELECT id_usuario, username, password_hash, nombre_completo, rol, activo "
                + "FROM " + TABLA + " WHERE LOWER(username) = LOWER(?)";

        try (PreparedStatement pstmt = obtenerConexion().prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearUsuario(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el usuario: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    /**
     * Convierte una fila del ResultSet en un objeto Usuario.
     */
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));
        usuario.setUsername(rs.getString("username"));
        usuario.setPasswordHash(rs.getString("password_hash"));
        usuario.setNombreCompleto(rs.getString("nombre_completo"));
        usuario.setRol(rs.getString("rol"));
        usuario.setActivo(rs.getBoolean("activo"));
        return usuario;
    }
}
