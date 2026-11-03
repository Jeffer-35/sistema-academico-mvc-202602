package com.uniajc.modelo;

// Clase que representa la entidad Estudiante en el sistema académico
public class Estudiante {

    // Atributos privados de la clase Estudiante
    private int idEstudiante;      // Identificador único del estudiante
    private String nombre;          // Nombre del estudiante
    private String apellido;        // Apellido del estudiante
    private String email;           // Correo electrónico del estudiante

    // Constructor vacío necesario para crear objetos sin parámetros
    public Estudiante() {
    }

    /**
     * Constructor con parámetros para inicializar todos los atributos.
     * @param idEstudiante Identificador único
     * @param nombre Nombre del estudiante
     * @param apellido Apellido del estudiante
     * @param email Correo electrónico
     */
    public Estudiante(int idEstudiante, String nombre, String apellido, String email) {
        this.idEstudiante = idEstudiante;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    // Métodos getter y setter para acceder y modificar los atributos privados

    /**
     * Obtiene el ID del estudiante.
     * @return idEstudiante
     */
    public int getIdEstudiante() {
        return idEstudiante;
    }

    /**
     * Establece el ID del estudiante.
     * @param idEstudiante nuevo ID
     */
    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    /**
     * Obtiene el nombre del estudiante.
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del estudiante.
     * @param nombre nuevo nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del estudiante.
     * @return apellido
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del estudiante.
     * @param apellido nuevo apellido
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el email del estudiante.
     * @return email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el email del estudiante.
     * @param email nuevo email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Representación en texto del objeto Estudiante.
     * @return String con los datos del estudiante
     */
    @Override
    public String toString() {
        return "Estudiante{" +
                "idEstudiante=" + idEstudiante +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}