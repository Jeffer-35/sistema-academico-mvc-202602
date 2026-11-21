package com.uniajc.modelo;

/**
 * Clase que representa un usuario del sistema (para el login).
 *
 * Tabla en la BD: "practica-mvc".usuarios
 * Columnas: id_usuario, username, password_hash, nombre_completo, rol, activo
 */
public class Usuario {

    private int idUsuario;          // Identificador único
    private String username;        // Nombre de usuario para entrar
    private String passwordHash;    // Contraseña cifrada con SHA-512 (nunca la real)
    private String nombreCompleto;  // Nombre que se muestra al entrar
    private String rol;             // ADMINISTRADOR, DOCENTE o ESTUDIANTE
    private boolean activo;         // Si el usuario puede entrar

    // Constructor vacío necesario para crear objetos sin parámetros
    public Usuario() {
    }

    /**
     * Constructor con parámetros para inicializar todos los atributos.
     */
    public Usuario(int idUsuario, String username, String passwordHash,
            String nombreCompleto, String rol, boolean activo) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    /**
     * Representación en texto. NO incluye el hash de la contraseña.
     */
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", username='" + username + '\'' +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", rol='" + rol + '\'' +
                ", activo=" + activo +
                '}';
    }
}
