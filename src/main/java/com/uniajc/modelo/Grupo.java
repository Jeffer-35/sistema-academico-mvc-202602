package com.uniajc.modelo;

/**
 * Entidad Grupo: una materia que dicta un docente en un aula y horario.
 * Tabla en la BD: "practica-mvc".grupo
 */
public class Grupo {

    private int idGrupo;       // Identificador único del grupo
    private int idMateria;     // Llave foránea hacia materia
    private int idDocente;     // Llave foránea hacia docente
    private String aula;       // Ejemplo: "B-204"
    private String horario;    // Ejemplo: "Lunes 6:00-8:00 pm"

    public Grupo() {
    }

    public Grupo(int idGrupo, int idMateria, int idDocente, String aula, String horario) {
        this.idGrupo = idGrupo;
        this.idMateria = idMateria;
        this.idDocente = idDocente;
        this.aula = aula;
        this.horario = horario;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(int idGrupo) {
        this.idGrupo = idGrupo;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(int idDocente) {
        this.idDocente = idDocente;
    }

    public String getAula() {
        return aula;
    }

    public void setAula(String aula) {
        this.aula = aula;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "Grupo " + idGrupo + " - Aula " + aula + " (" + horario + ")";
    }
}
