package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.InscripcionDao;
import com.uniajc.modelo.Estudiante;
import com.uniajc.modelo.Inscripcion;

/**
 * Pruebas unitarias de InscripcionService.
 * Usa DAO falsos en memoria para no tocar la base de datos real.
 */
class InscripcionServiceTest {

    private InscripcionDaoFalso inscripcionDao;
    private InscripcionService servicio;

    @BeforeEach
    void prepararServicio() {
        inscripcionDao = new InscripcionDaoFalso();
        EstudianteDaoFalso estudianteDao = new EstudianteDaoFalso();
        // Un estudiante que sí existe, con ID 1
        estudianteDao.guardar(new Estudiante(0, "Ana", "Martínez", "ana@email.com"));
        servicio = new InscripcionService(inscripcionDao, estudianteDao);
    }

    @Test
    @DisplayName("Registrar sin estado deja la inscripción como ACTIVO")
    void registrarSinEstadoQuedaActivo() {
        servicio.registrarInscripcion(new Inscripcion(0, 1, 1, null, null));

        assertEquals("ACTIVO", servicio.buscarInscripcionPorId(1).get().getEstado());
    }

    @Test
    @DisplayName("El estado se guarda en mayúsculas")
    void estadoEnMayusculas() {
        servicio.registrarInscripcion(new Inscripcion(0, 1, 1, 4.5, " aprobado "));

        assertEquals("APROBADO", servicio.buscarInscripcionPorId(1).get().getEstado());
    }

    @Test
    @DisplayName("No permite una nota mayor que 5")
    void notaMayorQueCinco() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new Inscripcion(0, 1, 1, 5.5, "ACTIVO")));

        assertEquals("La nota final debe estar entre 0 y 5.", error.getMessage());
    }

    @Test
    @DisplayName("No permite una nota negativa")
    void notaNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new Inscripcion(0, 1, 1, -1.0, "ACTIVO")));
    }

    @Test
    @DisplayName("No permite un estado que no existe")
    void estadoInvalido() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new Inscripcion(0, 1, 1, null, "PENDIENTE")));

        assertEquals("El estado debe ser ACTIVO, APROBADO, REPROBADO o CANCELADO.", error.getMessage());
    }

    @Test
    @DisplayName("No permite APROBADO con nota menor que 3")
    void aprobadoConNotaBaja() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new Inscripcion(0, 1, 1, 2.5, "APROBADO")));

        assertEquals("Para APROBADO la nota final debe ser 3.0 o más.", error.getMessage());
    }

    @Test
    @DisplayName("No permite inscribir dos veces al mismo estudiante en el mismo grupo")
    void inscripcionRepetida() {
        servicio.registrarInscripcion(new Inscripcion(0, 1, 1, null, "ACTIVO"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new Inscripcion(0, 1, 1, null, "ACTIVO")));

        assertEquals("El estudiante ya está inscrito en ese grupo.", error.getMessage());
    }

    @Test
    @DisplayName("No permite inscribir a un estudiante que no existe")
    void estudianteInexistente() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new Inscripcion(0, 99, 1, null, "ACTIVO")));

        assertEquals("El estudiante seleccionado no existe.", error.getMessage());
    }

    @Test
    @DisplayName("Si el grupo no existe, muestra un mensaje claro (error 23503)")
    void grupoInexistente() {
        inscripcionDao.simularGrupoInexistente = true;

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarInscripcion(new Inscripcion(0, 1, 50, null, "ACTIVO")));

        assertEquals("El grupo seleccionado no existe.", error.getMessage());
    }

    @Test
    @DisplayName("Permite actualizar la nota de una inscripción existente")
    void actualizarNota() {
        servicio.registrarInscripcion(new Inscripcion(0, 1, 1, null, "ACTIVO"));

        servicio.actualizarInscripcion(new Inscripcion(1, 1, 1, 4.2, "APROBADO"));

        assertEquals(Double.valueOf(4.2), servicio.buscarInscripcionPorId(1).get().getNotaFinal());
    }

    @Test
    @DisplayName("Eliminar una inscripción existente la borra")
    void eliminarInscripcion() {
        servicio.registrarInscripcion(new Inscripcion(0, 1, 1, null, "ACTIVO"));

        servicio.eliminarInscripcion(1);

        assertTrue(servicio.obtenerTodasLasInscripciones().isEmpty());
    }

    // ===================== DAO FALSOS =====================

    private static class InscripcionDaoFalso extends InscripcionDao {

        private final List<Inscripcion> lista = new ArrayList<>();
        private int siguienteId = 1;

        // Si es true, guardar() lanza el mismo error que PostgreSQL cuando el grupo no existe
        boolean simularGrupoInexistente = false;

        @Override
        public void guardar(Inscripcion inscripcion) {
            if (simularGrupoInexistente) {
                throw new RuntimeException("Error al guardar la inscripción",
                        new SQLException("viola llave foránea", "23503"));
            }
            inscripcion.setIdInscripcion(siguienteId++);
            lista.add(inscripcion);
        }

        @Override
        public List<Inscripcion> obtenerTodos() {
            return new ArrayList<>(lista);
        }

        @Override
        public Optional<Inscripcion> buscarPorId(int id) {
            return lista.stream().filter(i -> i.getIdInscripcion() == id).findFirst();
        }

        @Override
        public Optional<Inscripcion> buscarPorEstudianteYGrupo(int idEstudiante, int idGrupo) {
            return lista.stream()
                    .filter(i -> i.getIdEstudiante() == idEstudiante && i.getIdGrupo() == idGrupo)
                    .findFirst();
        }

        @Override
        public void actualizar(Inscripcion inscripcion) {
            lista.removeIf(i -> i.getIdInscripcion() == inscripcion.getIdInscripcion());
            lista.add(inscripcion);
        }

        @Override
        public void eliminar(int id) {
            lista.removeIf(i -> i.getIdInscripcion() == id);
        }
    }

    private static class EstudianteDaoFalso extends EstudianteDao {

        private final List<Estudiante> lista = new ArrayList<>();
        private int siguienteId = 1;

        @Override
        public void guardar(Estudiante estudiante) {
            estudiante.setIdEstudiante(siguienteId++);
            lista.add(estudiante);
        }

        @Override
        public List<Estudiante> obtenerTodos() {
            return new ArrayList<>(lista);
        }

        @Override
        public Optional<Estudiante> buscarPorId(int id) {
            return lista.stream().filter(e -> e.getIdEstudiante() == id).findFirst();
        }
    }
}
