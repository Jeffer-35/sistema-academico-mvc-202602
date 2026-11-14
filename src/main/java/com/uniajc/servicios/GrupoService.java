package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Grupo;

/**
 * Lógica de negocio de Grupo.
 * Además de GrupoDao usa MateriaDao y DocenteDao para comprobar
 * que la materia y el docente existan (integridad de las llaves foráneas).
 */
public class GrupoService {

    private final GrupoDao grupoDao;
    private final MateriaDao materiaDao;
    private final DocenteDao docenteDao;

    public GrupoService(GrupoDao grupoDao, MateriaDao materiaDao, DocenteDao docenteDao) {
        this.grupoDao = grupoDao;
        this.materiaDao = materiaDao;
        this.docenteDao = docenteDao;
    }

    public void registrarGrupo(Grupo grupo) {
        validar(grupo);
        if (grupoDao.buscarPorAulaYHorario(grupo.getAula(), grupo.getHorario()).isPresent()) {
            throw new IllegalArgumentException("Ya hay un grupo en esa aula y ese horario.");
        }
        grupoDao.guardar(grupo);
    }

    public List<Grupo> obtenerTodosLosGrupos() {
        return grupoDao.obtenerTodos();
    }

    public Optional<Grupo> buscarGrupoPorId(int id) {
        return grupoDao.buscarPorId(id);
    }

    public void actualizarGrupo(Grupo grupo) {
        if (grupo == null || grupo.getIdGrupo() <= 0) {
            throw new IllegalArgumentException("Grupo inválido.");
        }
        if (grupoDao.buscarPorId(grupo.getIdGrupo()).isEmpty()) {
            throw new IllegalArgumentException("Grupo no encontrado.");
        }
        validar(grupo);
        Optional<Grupo> cruce = grupoDao.buscarPorAulaYHorario(grupo.getAula(), grupo.getHorario());
        if (cruce.isPresent() && cruce.get().getIdGrupo() != grupo.getIdGrupo()) {
            throw new IllegalArgumentException("Ya hay otro grupo en esa aula y ese horario.");
        }
        grupoDao.actualizar(grupo);
    }

    public void eliminarGrupo(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        if (grupoDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("Grupo no encontrado.");
        }
        grupoDao.eliminar(id);
    }

    /** Validaciones comunes para registrar y actualizar. */
    private void validar(Grupo grupo) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo es obligatorio.");
        }
        if (materiaDao.buscarPorId(grupo.getIdMateria()).isEmpty()) {
            throw new IllegalArgumentException("La materia seleccionada no existe.");
        }
        if (docenteDao.buscarPorId(grupo.getIdDocente()).isEmpty()) {
            throw new IllegalArgumentException("El docente seleccionado no existe.");
        }

        String aula = grupo.getAula() == null ? "" : grupo.getAula().trim();
        if (aula.isEmpty() || aula.length() > 50) {
            throw new IllegalArgumentException("El aula es obligatoria (máximo 50 caracteres).");
        }
        String horario = grupo.getHorario() == null ? "" : grupo.getHorario().trim();
        if (horario.isEmpty() || horario.length() > 100) {
            throw new IllegalArgumentException("El horario es obligatorio (máximo 100 caracteres).");
        }

        // Se guardan sin espacios sobrantes
        grupo.setAula(aula);
        grupo.setHorario(horario);
    }
}
