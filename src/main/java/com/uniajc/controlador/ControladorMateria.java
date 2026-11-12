package com.uniajc.controlador;

import java.util.List;
import java.util.Optional;

import com.uniajc.modelo.Materia;
import com.uniajc.servicios.MateriaService;

public class ControladorMateria {

    private final MateriaService materiaService;

    public ControladorMateria(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    public void registrarMateria(String nombre, int creditos) {
        Materia materia = new Materia();
        materia.setNombreMateria(nombre);
        materia.setCreditos(creditos);
        materiaService.registrarMateria(materia);
    }

    public List<Materia> listarMaterias() {
        return materiaService.obtenerTodasLasMaterias();
    }

    public Optional<Materia> buscarMateriaPorId(int id) {
        return materiaService.buscarMateriaPorId(id);
    }

    public void actualizarMateria(int id, String nombre, int creditos) {
        Materia materia = new Materia(id, nombre, creditos);
        materiaService.actualizarMateria(materia);
    }

    public void eliminarMateria(int id) {
        materiaService.eliminarMateria(id);
    }
}