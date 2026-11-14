package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.uniajc.dao.DocenteDao;
import com.uniajc.modelo.Docente;

/**
 * Pruebas unitarias de DocenteService.
 * Usa un DAO falso en memoria para no tocar la base de datos real.
 */
class DocenteServiceTest {

    private DocenteDaoFalso daoFalso;
    private DocenteService servicio;

    @BeforeEach
    void prepararServicio() {
        daoFalso = new DocenteDaoFalso();
        servicio = new DocenteService(daoFalso);
    }

    @Test
    @DisplayName("Registrar un docente con título (Dr.) es válido")
    void registrarDocenteConTitulo() {
        servicio.registrarDocente(new Docente(0, "Dr. Juan Rodríguez", "Matemáticas"));

        assertEquals(1, servicio.obtenerTodosLosDocentes().size());
    }

    @Test
    @DisplayName("El servicio quita los espacios sobrantes antes de guardar")
    void registrarQuitaEspacios() {
        servicio.registrarDocente(new Docente(0, "   Ana Pérez   ", "  Bases de Datos "));

        Docente guardado = servicio.buscarDocentePorId(1).get();
        assertEquals("Ana Pérez", guardado.getNombre());
        assertEquals("Bases de Datos", guardado.getEspecialidad());
    }

    @Test
    @DisplayName("No permite registrar un docente sin especialidad")
    void registrarSinEspecialidad() {
        Docente docente = new Docente(0, "Ana Pérez", "   ");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarDocente(docente));

        assertEquals("La especialidad es obligatoria.", error.getMessage());
    }

    @Test
    @DisplayName("No permite un nombre con números")
    void registrarNombreConNumeros() {
        Docente docente = new Docente(0, "Ana 123", "Programación");

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarDocente(docente));
    }

    @Test
    @DisplayName("No permite actualizar un docente que no existe")
    void actualizarDocenteInexistente() {
        Docente docente = new Docente(99, "Ana Pérez", "Programación");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarDocente(docente));

        assertEquals("Docente no encontrado.", error.getMessage());
    }

    @Test
    @DisplayName("No permite eliminar un docente que no existe")
    void eliminarDocenteInexistente() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminarDocente(5));

        assertEquals("Docente no encontrado.", error.getMessage());
    }

    @Test
    @DisplayName("Si el docente tiene grupos, muestra un mensaje claro al eliminar")
    void eliminarDocenteConGrupos() {
        servicio.registrarDocente(new Docente(0, "Ana Pérez", "Programación"));
        daoFalso.simularDocenteConGrupos = true; // Simula el error 23503 de PostgreSQL

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminarDocente(1));

        assertEquals("No se puede eliminar: el docente está asignado a uno o más grupos.",
                error.getMessage());
    }

    // ===================== DAO FALSO =====================

    private static class DocenteDaoFalso extends DocenteDao {

        private final List<Docente> lista = new ArrayList<>();
        private int siguienteId = 1;

        // Si es true, eliminar() lanza el mismo error que PostgreSQL con llave foránea
        boolean simularDocenteConGrupos = false;

        @Override
        public void guardar(Docente docente) {
            docente.setIdDocente(siguienteId++);
            lista.add(docente);
        }

        @Override
        public List<Docente> obtenerTodos() {
            return new ArrayList<>(lista);
        }

        @Override
        public Optional<Docente> buscarPorId(int id) {
            return lista.stream().filter(d -> d.getIdDocente() == id).findFirst();
        }

        @Override
        public void actualizar(Docente docente) {
            lista.removeIf(d -> d.getIdDocente() == docente.getIdDocente());
            lista.add(docente);
        }

        @Override
        public void eliminar(int id) {
            if (simularDocenteConGrupos) {
                throw new RuntimeException("Error al eliminar el docente",
                        new SQLException("viola llave foránea", "23503"));
            }
            lista.removeIf(d -> d.getIdDocente() == id);
        }
    }
}
