package com.uniajc.servicios;

import java.util.List;
import java.util.Optional;

import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;

/**
 * Lógica de negocio de Materia: validaciones de integridad.
 */
public class MateriaService {

    private final MateriaDao materiaDao;

    public MateriaService(MateriaDao materiaDao) {
        this.materiaDao = materiaDao;
    }

    public void registrarMateria(Materia materia) {
        validar(materia);
        if (materiaDao.buscarPorNombre(materia.getNombreMateria().trim()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una materia con ese nombre.");
        }
        materia.setNombreMateria(materia.getNombreMateria().trim());
        materiaDao.guardar(materia);
    }

    public List<Materia> obtenerTodasLasMaterias() {
        return materiaDao.obtenerTodos();
    }

    public Optional<Materia> buscarMateriaPorId(int id) {
        return materiaDao.buscarPorId(id);
    }

    public void actualizarMateria(Materia materia) {
        if (materia == null || materia.getIdMateria() <= 0) {
            throw new IllegalArgumentException("Materia inválida.");
        }
        if (materiaDao.buscarPorId(materia.getIdMateria()).isEmpty()) {
            throw new IllegalArgumentException("Materia no encontrada.");
        }
        validar(materia);
        Optional<Materia> conMismoNombre = materiaDao.buscarPorNombre(materia.getNombreMateria().trim());
        if (conMismoNombre.isPresent() && conMismoNombre.get().getIdMateria() != materia.getIdMateria()) {
            throw new IllegalArgumentException("Ya existe otra materia con ese nombre.");
        }
        materia.setNombreMateria(materia.getNombreMateria().trim());
        materiaDao.actualizar(materia);
    }

    public void eliminarMateria(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        if (materiaDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("Materia no encontrada.");
        }
        materiaDao.eliminar(id);
    }

    private void validar(Materia materia) {
        if (materia == null) {
            throw new IllegalArgumentException("La materia es obligatoria.");
        }
        String nombre = materia.getNombreMateria();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la materia es obligatorio.");
        }
        if (nombre.trim().length() < 3 || nombre.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 3 y 100 caracteres.");
        }
        if (materia.getCreditos() < 1 || materia.getCreditos() > 10) {
            throw new IllegalArgumentException("Los créditos deben estar entre 1 y 10.");
        }
    }
}