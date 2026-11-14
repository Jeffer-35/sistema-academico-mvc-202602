package com.uniajc.servicios;

// Importaciones necesarias
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// Importar los DAO y el modelo
import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.InscripcionDao;
import com.uniajc.modelo.Inscripcion;

/**
 * Capa de Servicio para la entidad Inscripcion.
 * Aquí van las validaciones y las reglas de negocio:
 *  - El estudiante debe existir.
 *  - El grupo debe existir (lo verifica la llave foránea de la base de datos).
 *  - Un estudiante no puede inscribirse dos veces en el mismo grupo.
 *  - La nota final va de 0 a 5 (puede estar vacía).
 *  - El estado solo puede ser ACTIVO, APROBADO, REPROBADO o CANCELADO.
 *  - Para APROBADO la nota debe ser 3.0 o más; para REPROBADO, menor que 3.0.
 */
public class InscripcionService {

    // Estados permitidos
    private static final Set<String> ESTADOS_VALIDOS = Set.of(
            Inscripcion.ACTIVO, Inscripcion.APROBADO, Inscripcion.REPROBADO, Inscripcion.CANCELADO);

    // Escala de notas en Colombia: de 0 a 5, se aprueba con 3.0
    private static final double NOTA_MINIMA = 0.0;
    private static final double NOTA_MAXIMA = 5.0;
    private static final double NOTA_APROBATORIA = 3.0;

    // Códigos de error de PostgreSQL
    private static final String ERROR_LLAVE_FORANEA = "23503"; // el grupo no existe
    private static final String ERROR_DUPLICADO = "23505";     // ya existe la misma inscripción

    private final InscripcionDao inscripcionDao;
    private final EstudianteDao estudianteDao;

    /**
     * Constructor: recibe los DAO que va a utilizar.
     * @param inscripcionDao DAO de inscripciones
     * @param estudianteDao DAO de estudiantes (para verificar que el estudiante exista)
     */
    public InscripcionService(InscripcionDao inscripcionDao, EstudianteDao estudianteDao) {
        this.inscripcionDao = inscripcionDao;
        this.estudianteDao = estudianteDao;
    }

    /**
     * Registra una nueva inscripción después de validar sus datos.
     * Si no trae estado, queda como ACTIVO.
     * @param inscripcion Inscripción a registrar
     */
    public void registrarInscripcion(Inscripcion inscripcion) {
        validarInscripcion(inscripcion);
        validarNoRepetida(inscripcion);
        try {
            inscripcionDao.guardar(inscripcion);
        } catch (RuntimeException ex) {
            throw traducirError(ex);
        }
    }

    /**
     * Obtiene todas las inscripciones.
     * @return Lista de inscripciones
     */
    public List<Inscripcion> obtenerTodasLasInscripciones() {
        return inscripcionDao.obtenerTodos();
    }

    /**
     * Busca una inscripción por su ID.
     * @param id Identificador de la inscripción
     * @return Optional con la inscripción, vacío si no existe
     */
    public Optional<Inscripcion> buscarInscripcionPorId(int id) {
        return inscripcionDao.buscarPorId(id);
    }

    /**
     * Actualiza una inscripción existente (por ejemplo, para poner la nota final).
     * @param inscripcion Inscripción con los datos nuevos
     */
    public void actualizarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null || inscripcion.getIdInscripcion() <= 0) {
            throw new IllegalArgumentException("Inscripción inválida.");
        }
        if (inscripcionDao.buscarPorId(inscripcion.getIdInscripcion()).isEmpty()) {
            throw new IllegalArgumentException("Inscripción no encontrada.");
        }

        validarInscripcion(inscripcion);
        validarNoRepetida(inscripcion);
        try {
            inscripcionDao.actualizar(inscripcion);
        } catch (RuntimeException ex) {
            throw traducirError(ex);
        }
    }

    /**
     * Elimina una inscripción por su ID, verificando que exista.
     * @param id Identificador de la inscripción
     */
    public void eliminarInscripcion(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }
        if (inscripcionDao.buscarPorId(id).isEmpty()) {
            throw new IllegalArgumentException("Inscripción no encontrada.");
        }
        inscripcionDao.eliminar(id);
    }

    /**
     * Valida los datos de la inscripción.
     * También deja el estado en mayúsculas y sin espacios.
     */
    private void validarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripción no puede ser nula.");
        }

        // Estudiante y grupo obligatorios
        if (inscripcion.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un estudiante.");
        }
        if (inscripcion.getIdGrupo() <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un grupo.");
        }

        // El estudiante debe existir
        if (estudianteDao.buscarPorId(inscripcion.getIdEstudiante()).isEmpty()) {
            throw new IllegalArgumentException("El estudiante seleccionado no existe.");
        }

        // Nota: puede estar vacía, pero si tiene valor debe estar entre 0 y 5
        Double nota = inscripcion.getNotaFinal();
        if (nota != null && (nota < NOTA_MINIMA || nota > NOTA_MAXIMA)) {
            throw new IllegalArgumentException("La nota final debe estar entre 0 y 5.");
        }

        // Estado: si viene vacío queda ACTIVO; si no, debe ser uno de los permitidos
        String estado = inscripcion.getEstado();
        if (estado == null || estado.trim().isEmpty()) {
            estado = Inscripcion.ACTIVO;
        }
        estado = estado.trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(estado)) {
            throw new IllegalArgumentException(
                    "El estado debe ser ACTIVO, APROBADO, REPROBADO o CANCELADO.");
        }
        inscripcion.setEstado(estado);

        // Coherencia entre la nota y el estado
        if (Inscripcion.APROBADO.equals(estado)) {
            if (nota == null || nota < NOTA_APROBATORIA) {
                throw new IllegalArgumentException("Para APROBADO la nota final debe ser 3.0 o más.");
            }
        }
        if (Inscripcion.REPROBADO.equals(estado)) {
            if (nota == null || nota >= NOTA_APROBATORIA) {
                throw new IllegalArgumentException("Para REPROBADO la nota final debe ser menor que 3.0.");
            }
        }
    }

    /**
     * Regla de negocio: un estudiante no puede inscribirse dos veces en el mismo grupo.
     * Al actualizar, se ignora la propia inscripción.
     */
    private void validarNoRepetida(Inscripcion inscripcion) {
        Optional<Inscripcion> existente = inscripcionDao.buscarPorEstudianteYGrupo(
                inscripcion.getIdEstudiante(), inscripcion.getIdGrupo());

        if (existente.isPresent()
                && existente.get().getIdInscripcion() != inscripcion.getIdInscripcion()) {
            throw new IllegalArgumentException("El estudiante ya está inscrito en ese grupo.");
        }
    }

    /**
     * Convierte los errores técnicos de PostgreSQL en mensajes claros para el usuario.
     */
    private RuntimeException traducirError(RuntimeException ex) {
        if (ex.getCause() instanceof SQLException sqlEx) {
            if (ERROR_LLAVE_FORANEA.equals(sqlEx.getSQLState())) {
                return new IllegalArgumentException("El grupo seleccionado no existe.");
            }
            if (ERROR_DUPLICADO.equals(sqlEx.getSQLState())) {
                return new IllegalArgumentException("El estudiante ya está inscrito en ese grupo.");
            }
        }
        return ex;
    }
}
