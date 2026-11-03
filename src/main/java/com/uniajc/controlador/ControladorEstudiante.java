package com.uniajc.controlador;

// Importaciones necesarias
import java.util.List;
import java.util.Optional;

// Importar el modelo y el servicio
import com.uniajc.modelo.Estudiante;
import com.uniajc.servicios.EstudianteService;

/**
 * Controlador de la entidad Estudiante.
 * Es el intermediario entre la Vista y el Servicio:
 * recibe los datos que escribe el usuario en la vista,
 * arma el objeto Estudiante y se lo pasa al Servicio.
 * El controlador NO valida ni guarda; eso lo hacen el Servicio y el DAO.
 */
public class ControladorEstudiante {

    // El controlador usa el Servicio para hacer las operaciones
    private final EstudianteService estudianteService;

    /**
     * Constructor: recibe el Servicio que va a utilizar.
     * @param estudianteService Servicio de estudiantes
     */
    public ControladorEstudiante(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    /**
     * Registra un estudiante nuevo con los datos que llegan de la vista.
     * El id no se envía porque lo genera la base de datos.
     */
    public void registrarEstudiante(String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante();
        estudiante.setNombre(nombre);
        estudiante.setApellido(apellido);
        estudiante.setEmail(email);
        estudianteService.registrarEstudiante(estudiante);
    }

    /**
     * Devuelve la lista de todos los estudiantes para mostrarla en la vista.
     * @return Lista de estudiantes
     */
    public List<Estudiante> listarEstudiantes() {
        return estudianteService.obtenerTodosLosEstudiantes();
    }

    /**
     * Busca un estudiante por su id.
     * @param id Identificador del estudiante
     * @return Optional con el estudiante, vacío si no existe
     */
    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        return estudianteService.buscarEstudiantePorId(id);
    }

    /**
     * Actualiza un estudiante existente con los datos nuevos de la vista.
     */
    public void actualizarEstudiante(int id, String nombre, String apellido, String email) {
        Estudiante estudiante = new Estudiante(id, nombre, apellido, email);
        estudianteService.actualizarEstudiante(estudiante);
    }

    /**
     * Elimina un estudiante por su id.
     * @param id Identificador del estudiante
     */
    public void eliminarEstudiante(int id) {
        estudianteService.eliminarEstudiante(id);
    }
}