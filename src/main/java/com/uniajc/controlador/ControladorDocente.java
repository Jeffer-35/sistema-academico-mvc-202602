package com.uniajc.controlador;

// Importaciones necesarias
import java.util.List;
import java.util.Optional;

// Importar el modelo y el servicio
import com.uniajc.modelo.Docente;
import com.uniajc.servicios.DocenteService;

/**
 * Controlador de la entidad Docente.
 * Es el intermediario entre la Vista y el Servicio:
 * recibe los datos que escribe el usuario en la vista,
 * arma el objeto Docente y se lo pasa al Servicio.
 * El controlador NO valida ni guarda; eso lo hacen el Servicio y el DAO.
 */
public class ControladorDocente {

    // El controlador usa el Servicio para hacer las operaciones
    private final DocenteService docenteService;

    /**
     * Constructor: recibe el Servicio que va a utilizar.
     * @param docenteService Servicio de docentes
     */
    public ControladorDocente(DocenteService docenteService) {
        this.docenteService = docenteService;
    }

    /**
     * Registra un docente nuevo con los datos que llegan de la vista.
     * El id no se envía porque lo genera la base de datos.
     */
    public void registrarDocente(String nombre, String especialidad) {
        Docente docente = new Docente();
        docente.setNombre(nombre);
        docente.setEspecialidad(especialidad);
        docenteService.registrarDocente(docente);
    }

    /**
     * Devuelve la lista de todos los docentes para mostrarla en la vista.
     * @return Lista de docentes
     */
    public List<Docente> listarDocentes() {
        return docenteService.obtenerTodosLosDocentes();
    }

    /**
     * Busca un docente por su id.
     * @param id Identificador del docente
     * @return Optional con el docente, vacío si no existe
     */
    public Optional<Docente> buscarDocentePorId(int id) {
        return docenteService.buscarDocentePorId(id);
    }

    /**
     * Actualiza un docente existente con los datos nuevos de la vista.
     */
    public void actualizarDocente(int id, String nombre, String especialidad) {
        Docente docente = new Docente(id, nombre, especialidad);
        docenteService.actualizarDocente(docente);
    }

    /**
     * Elimina un docente por su id.
     * @param id Identificador del docente
     */
    public void eliminarDocente(int id) {
        docenteService.eliminarDocente(id);
    }
}