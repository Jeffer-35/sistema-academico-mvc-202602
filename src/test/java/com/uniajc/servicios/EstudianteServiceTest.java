package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.uniajc.dao.EstudianteDao;
import com.uniajc.modelo.Estudiante;

/**
 * Pruebas unitarias de EstudianteService.
 * Se usa un DAO falso que guarda los datos en una lista en memoria,
 * para probar SOLO la lógica del servicio sin tocar la base de datos real.
 *
 * Cada prueba sigue el patrón AAA:
 *  1. Preparar (Arrange): crear los datos
 *  2. Ejecutar (Act): llamar al método que se prueba
 *  3. Verificar (Assert): comprobar el resultado
 */
class EstudianteServiceTest {

    private EstudianteService servicio;

    /**
     * Se ejecuta ANTES de cada prueba: deja todo limpio para que
     * una prueba no afecte a la otra.
     */
    @BeforeEach
    void prepararServicio() {
        servicio = new EstudianteService(new EstudianteDaoFalso());
    }

    // ===================== REGISTRAR =====================

    @Test
    @DisplayName("Registrar un estudiante válido lo guarda")
    void registrarEstudianteValido() {
        servicio.registrarEstudiante(new Estudiante(0, "Ana", "Martínez", "ana@email.com"));

        assertEquals(1, servicio.obtenerTodosLosEstudiantes().size());
    }

    @Test
    @DisplayName("No permite registrar un estudiante sin nombre")
    void registrarSinNombre() {
        Estudiante estudiante = new Estudiante(0, "", "Martínez", "ana@email.com");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(estudiante));

        assertEquals("El nombre es obligatorio.", error.getMessage());
    }

    @Test
    @DisplayName("No permite un nombre con números")
    void registrarNombreConNumeros() {
        Estudiante estudiante = new Estudiante(0, "Ana123", "Martínez", "ana@email.com");

        assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(estudiante));
    }

    @Test
    @DisplayName("No permite un email sin formato válido")
    void registrarEmailInvalido() {
        Estudiante estudiante = new Estudiante(0, "Ana", "Martínez", "ana.email.com");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(estudiante));

        assertEquals("El formato del email es inválido.", error.getMessage());
    }

    @Test
    @DisplayName("No permite dos estudiantes con el mismo email")
    void registrarEmailRepetido() {
        servicio.registrarEstudiante(new Estudiante(0, "Ana", "Martínez", "ana@email.com"));
        Estudiante otro = new Estudiante(0, "Juan", "López", "ANA@email.com");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.registrarEstudiante(otro));

        assertEquals("Ya existe un estudiante con ese email.", error.getMessage());
    }

    // ===================== ACTUALIZAR =====================

    @Test
    @DisplayName("No permite actualizar un estudiante que no existe")
    void actualizarEstudianteInexistente() {
        Estudiante estudiante = new Estudiante(99, "Ana", "Martínez", "ana@email.com");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.actualizarEstudiante(estudiante));

        assertEquals("Estudiante no encontrado.", error.getMessage());
    }

    @Test
    @DisplayName("Permite actualizar un estudiante conservando su mismo email")
    void actualizarConSuMismoEmail() {
        servicio.registrarEstudiante(new Estudiante(0, "Ana", "Martínez", "ana@email.com"));

        servicio.actualizarEstudiante(new Estudiante(1, "Ana María", "Martínez", "ana@email.com"));

        assertEquals("Ana María", servicio.buscarEstudiantePorId(1).get().getNombre());
    }

    // ===================== ELIMINAR Y BUSCAR =====================

    @Test
    @DisplayName("No permite eliminar con un ID inválido")
    void eliminarIdInvalido() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.eliminarEstudiante(0));

        assertEquals("ID inválido.", error.getMessage());
    }

    @Test
    @DisplayName("Eliminar un estudiante existente lo borra")
    void eliminarEstudianteExistente() {
        servicio.registrarEstudiante(new Estudiante(0, "Ana", "Martínez", "ana@email.com"));

        servicio.eliminarEstudiante(1);

        assertTrue(servicio.obtenerTodosLosEstudiantes().isEmpty());
    }

    @Test
    @DisplayName("Buscar un ID que no existe devuelve vacío")
    void buscarIdInexistente() {
        Optional<Estudiante> resultado = servicio.buscarEstudiantePorId(50);

        assertTrue(resultado.isEmpty());
    }

    // ===================== DAO FALSO =====================

    /**
     * DAO falso: hereda de EstudianteDao pero guarda todo en una lista,
     * así las pruebas no se conectan a la base de datos.
     */
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

        @Override
        public void actualizar(Estudiante estudiante) {
            lista.removeIf(e -> e.getIdEstudiante() == estudiante.getIdEstudiante());
            lista.add(estudiante);
        }

        @Override
        public void eliminar(int id) {
            lista.removeIf(e -> e.getIdEstudiante() == id);
        }
    }
}
