package com.uniajc.controlador;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.uniajc.modelo.Grupo;
import com.uniajc.servicios.GrupoService;

/**
 * Controlador de Grupo: recibe los datos de la vista,
 * arma el objeto Grupo y se lo pasa al servicio.
 */
public class ControladorGrupo {

    private final GrupoService grupoService;

    public ControladorGrupo(GrupoService grupoService) {
        this.grupoService = grupoService;
    }

    public void registrarGrupo(int idMateria, int idDocente, String aula, String horario) {
        Grupo grupo = new Grupo();
        grupo.setIdMateria(idMateria);
        grupo.setIdDocente(idDocente);
        grupo.setAula(aula);
        grupo.setHorario(horario);
        grupoService.registrarGrupo(grupo);
    }

    public List<Grupo> listarGrupos() {
        return grupoService.obtenerTodosLosGrupos();
    }

    /**
     * Devuelve los grupos para el combo (id -> descripción).
     * Los datos vienen del procedimiento almacenado fn_listar_grupos_combo().
     */
    public Map<Integer, String> listarGruposParaCombo() {
        return grupoService.listarGruposParaCombo();
    }

    public Optional<Grupo> buscarGrupoPorId(int id) {
        return grupoService.buscarGrupoPorId(id);
    }

    public void actualizarGrupo(int id, int idMateria, int idDocente, String aula, String horario) {
        Grupo grupo = new Grupo(id, idMateria, idDocente, aula, horario);
        grupoService.actualizarGrupo(grupo);
    }

    public void eliminarGrupo(int id) {
        grupoService.eliminarGrupo(id);
    }
}
