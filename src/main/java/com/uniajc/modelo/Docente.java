package com.uniajc.modelo;

// Clase que representa la entidad Docente en el sistema académico
public class Docente {

    // Atributos privados de la clase Docente
    private int idDocente;          // Identificador único del docente
    private String nombre;          // Nombre completo del docente
    private String especialidad;    // Área o especialidad del docente

    // Constructor vacío necesario para crear objetos sin parámetros
    public Docente() {
    }

    /**
     * Constructor con parámetros para inicializar todos los atributos.
     * @param idDocente Identificador único
     * @param nombre Nombre del docente
     * @param especialidad Especialidad del docente
     */
    public Docente(int idDocente, String nombre, String especialidad) {
        this.idDocente = idDocente;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    // Métodos getter y setter para acceder y modificar los atributos privados

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    /**
     * Representación en texto del objeto Docente.
     * @return String con los datos del docente
     */
    @Override
    public String toString() {
        return "Docente{" +
                "idDocente=" + idDocente +
                ", nombre='" + nombre + '\'' +
                ", especialidad='" + especialidad + '\'' +
                '}';
    }
}