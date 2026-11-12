package com.uniajc.servicios;

// Importaciones necesarias
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

// Importar el DAO y el modelo
import com.uniajc.dao.DocenteDao;
import com.uniajc.modelo.Docente;

/**
 * Capa de Servicio para la entidad Docente.
 * Aquí van las validaciones y las reglas de negocio.
 * El DAO solo guarda y consulta; el Servicio decide si los datos son válidos.
 */
public class DocenteService {

    // Nombre: letras (con tildes), espacios y puntos (para "Dr." o "Ing."), entre 2 y 100 caracteres
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^[\\p{L} .]{2,100}$");

    // Especialidad: letras, números, espacios y puntos (por ejemplo "Programación II"), entre 2 y 100 caracteres
    private static final Pattern ESPECIALIDAD_PATTERN = Pattern.compile("^[\\p{L}0-9 .]{2,100}$");

    // Código de PostgreSQL cuando un registro está siendo usado por otra tabla (llave foránea)
    private static final String ERROR_LLAVE_FORANEA = "23503";

    // El Servicio usa el DAO para acceder a la base de datos
    private final DocenteDao docenteDao;

    /**
     * Constructor: recibe el DAO que va a utilizar.
     * @param docenteDao DAO de docentes
     */
    public DocenteService(DocenteDao docenteDao) {
        this.docenteDao = docenteDao;
    }

    /**
     * Registra un nuevo docente después de validar sus datos.
     * @param docente Docente a registrar
     */
    public void registrarDocente(Docente docente) {
        validarDocente(docente);
        docenteDao.guardar(docente);
    }

    /**
     * Obtiene todos los docentes.
     * @return Lista de docentes
     */
    public List<Docente> obtenerTodosLosDocentes() {
        return docenteDao.obtenerTodos();
    }

    /**
     * Busca un docente por su ID.
     * @param id Identificador del docente
     * @return Optional con el docente, vacío si no existe
     */
    public Optional<Docente> buscarDocentePorId(int id) {
        return docenteDao.buscarPorId(id);
    }

    /**
     * Actualiza un docente existente después de validar sus datos.
     * @param docente Docente con los datos nuevos
     */
    public void actualizarDocente(Docente docente) {
        if (docente == null || docente.getIdDocente() <= 0) {
            throw new IllegalArgumentException("Docente inválido.");
        }

        // Verificar que el docente exista antes de actualizar
        Optional<Docente> existente = docenteDao.buscarPorId(docente.getIdDocente());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Docente no encontrado.");
        }

        validarDocente(docente);
        docenteDao.actualizar(docente);
    }

    /**
     * Elimina un docente por su ID, verificando que exista.
     * Si el docente está asignado a un grupo, la base de datos no deja borrarlo,
     * y se muestra un mensaje claro en vez del error técnico.
     * @param id Identificador del docente
     */
    public void eliminarDocente(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Optional<Docente> existente = docenteDao.buscarPorId(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Docente no encontrado.");
        }

        try {
            docenteDao.eliminar(id);
        } catch (RuntimeException ex) {
            // Revisar si el error fue porque el docente está asignado a un grupo
            if (ex.getCause() instanceof SQLException sqlEx
                    && ERROR_LLAVE_FORANEA.equals(sqlEx.getSQLState())) {
                throw new IllegalArgumentException(
                        "No se puede eliminar: el docente está asignado a uno o más grupos.");
            }
            throw ex; // Si fue otro error, se deja pasar tal cual
        }
    }

    /**
     * Valida que los datos del docente cumplan las reglas.
     * También quita los espacios sobrantes al inicio y al final.
     * @param docente Docente a validar
     */
    private void validarDocente(Docente docente) {
        if (docente == null) {
            throw new IllegalArgumentException("El docente no puede ser nulo.");
        }

        // Nombre obligatorio y con formato válido
        if (docente.getNombre() == null || docente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        docente.setNombre(docente.getNombre().trim());
        if (!NOMBRE_PATTERN.matcher(docente.getNombre()).matches()) {
            throw new IllegalArgumentException(
                    "El nombre solo puede tener letras, espacios y puntos (2 a 100 caracteres).");
        }

        // Especialidad obligatoria y con formato válido
        if (docente.getEspecialidad() == null || docente.getEspecialidad().trim().isEmpty()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
        docente.setEspecialidad(docente.getEspecialidad().trim());
        if (!ESPECIALIDAD_PATTERN.matcher(docente.getEspecialidad()).matches()) {
            throw new IllegalArgumentException(
                    "La especialidad solo puede tener letras, números, espacios y puntos (2 a 100 caracteres).");
        }
    }
}