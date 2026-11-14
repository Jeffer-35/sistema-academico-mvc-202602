package com.uniajc.modelo;

/**
 * Clase que representa una inscripción: un estudiante inscrito en un grupo.
 *
 * Tabla en la BD: "practica-mvc".inscripcion_curso
 * Columnas: id_inscripcion, id_estudiante, id_grupo, nota_final, estado
 */
public class Inscripcion {

    // Estados permitidos para una inscripción
    public static final String ACTIVO = "ACTIVO";
    public static final String APROBADO = "APROBADO";
    public static final String REPROBADO = "REPROBADO";
    public static final String CANCELADO = "CANCELADO";

    private int idInscripcion;   // Identificador único de la inscripción
    private int idEstudiante;    // Estudiante inscrito (llave foránea a estudiante)
    private int idGrupo;         // Grupo en el que se inscribe (llave foránea a grupo)
    private Double notaFinal;    // Nota de 0 a 5. Es Double (no double) porque puede estar vacía
    private String estado;       // ACTIVO, APROBADO, REPROBADO o CANCELADO

    // Constructor vacío necesario para crear objetos sin parámetros
    public Inscripcion() {
    }

    /**
     * Constructor con parámetros para inicializar todos los atributos.
     */
    public Inscripcion(int idInscripcion, int idEstudiante, int idGrupo, Double notaFinal, String estado) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.idGrupo = idGrupo;
        this.notaFinal = notaFinal;
        this.estado = estado;
    }

    // Métodos getter y setter

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public Double getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Double notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Inscripcion{" +
                "idInscripcion=" + idInscripcion +
                ", idEstudiante=" + idEstudiante +
                ", idGrupo=" + idGrupo +
                ", notaFinal=" + notaFinal +
                ", estado='" + estado + '\'' +
                '}';
    }
}
