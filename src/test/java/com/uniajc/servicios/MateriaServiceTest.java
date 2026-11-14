package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.uniajc.dao.MateriaDao;
import com.uniajc.modelo.Materia;

/**
 * Pruebas unitarias de MateriaService.
 * Usa un DAO falso en memoria para no tocar la base de datos real.
 */
class MateriaServiceTest {

    private MateriaService servicio;

    @BeforeEach
    void prepararServicio() {
        servicio = new MateriaService(new MateriaDaoFalso());
    }

    @Test
    @DisplayName("Registrar una materia válida la guarda sin espacios sobrantes")
    void registrarMateriaValida() {
        servicio.registrarMateria(new Materia(0, "  Programación II  ", 3));

        assertEquals(1, servicio.obtenerTodasLasMaterias().size());
        assertEquals("Programación II", servicio.buscarMateriaPorId(1).get().getNombreMateria());
    }

    @Test
    @DisplayName("No permite una materia sin nombre")
    void registrarSinNombre() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarMateria(new Materia(0, "  ", 3)));

        assertEquals("El nombre de la materia es obligatorio.", error.getMessage());
    }

    @Test
    @DisplayName("No permite créditos fuera del rango de 1 a 10")
    void registrarCreditosInvalidos() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarMateria(new Materia(0, "Cálculo", 11)));

        assertEquals("Los créditos deben estar entre 1 y 10.", error.getMessage());
    }

    @Test
    @DisplayName("No permite dos materias con el mismo nombre")
    void registrarNombreRepetido() {
        servicio.registrarMateria(new Materia(0, "Cálculo", 4));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarMateria(new Materia(0, "Cálculo", 3)));

        assertEquals("Ya existe una materia con ese nombre.", error.getMessage());
    }

    @Test
    @DisplayName("No permite eliminar una materia que no existe")
    void eliminarMateriaInexistente() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminarMateria(7));

        assertEquals("Materia no encontrada.", error.getMessage());
    }

    // ===================== DAO FALSO =====================

    private static class MateriaDaoFalso extends MateriaDao {

        private final List<Materia> lista = new ArrayList<>();
        private int siguienteId = 1;

        @Override
        public void guardar(Materia materia) {
            materia.setIdMateria(siguienteId++);
            lista.add(materia);
        }

        @Override
        public List<Materia> obtenerTodos() {
            return new ArrayList<>(lista);
        }

        @Override
        public Optional<Materia> buscarPorId(int id) {
            return lista.stream().filter(m -> m.getIdMateria() == id).findFirst();
        }

        @Override
        public Optional<Materia> buscarPorNombre(String nombre) {
            return lista.stream().filter(m -> m.getNombreMateria().equalsIgnoreCase(nombre)).findFirst();
        }

        @Override
        public void actualizar(Materia materia) {
            lista.removeIf(m -> m.getIdMateria() == materia.getIdMateria());
            lista.add(materia);
        }

        @Override
        public void eliminar(int id) {
            lista.removeIf(m -> m.getIdMateria() == id);
        }
    }
}
