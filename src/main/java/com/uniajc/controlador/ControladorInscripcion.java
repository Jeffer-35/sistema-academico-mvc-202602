package com.uniajc.controlador;

// Importaciones necesarias
import java.util.List;
import java.util.Optional;

// Importar el modelo y el servicio
import com.uniajc.modelo.Inscripcion;
import com.uniajc.servicios.InscripcionService;

/**
 * Controlador de la entidad Inscripcion.
 * Es el intermediario entre la Vista y el Servicio:
 * recibe los datos que elige el usuario en la vista,
 * arma el objeto Inscripcion y se lo pasa al Servicio.
 * El controlador NO valida ni guarda; eso lo hacen el Servicio y el DAO.
 */
public class ControladorInscripcion {

    // El controlador usa el Servicio para hacer las operaciones
    private final InscripcionService inscripcionService;

    /**
     * Constructor: recibe el Servicio que va a utilizar.
     * @param inscripcionService Servicio de inscripciones
     */
    public ControladorInscripcion(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    /**
     * Inscribe un estudiante en un grupo.
     * La nota puede ir vacía (null) y el estado, si va vacío, queda ACTIVO.
     */
    public void registrarInscripcion(int idEstudiante, int idGrupo, Double notaFinal, String estado) {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdEstudiante(idEstudiante);
        inscripcion.setIdGrupo(idGrupo);
        inscripcion.setNotaFinal(notaFinal);
        inscripcion.setEstado(estado);
        inscripcionService.registrarInscripcion(inscripcion);
    }

    /**
     * Devuelve todas las inscripciones para mostrarlas en la vista.
     */
    public List<Inscripcion> listarInscripciones() {
        return inscripcionService.obtenerTodasLasInscripciones();
    }

    /**
     * Busca una inscripción por su id.
     */
    public Optional<Inscripcion> buscarInscripcionPorId(int id) {
        return inscripcionService.buscarInscripcionPorId(id);
    }

    /**
     * Actualiza una inscripción (por ejemplo, para poner la nota final y el estado).
     */
    public void actualizarInscripcion(int id, int idEstudiante, int idGrupo, Double notaFinal, String estado) {
        Inscripcion inscripcion = new Inscripcion(id, idEstudiante, idGrupo, notaFinal, estado);
        inscripcionService.actualizarInscripcion(inscripcion);
    }

    /**
     * Elimina una inscripción por su id.
     */
    public void eliminarInscripcion(int id) {
        inscripcionService.eliminarInscripcion(id);
    }
}
