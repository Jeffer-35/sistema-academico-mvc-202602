package com.uniajc.servicios;

// Importaciones necesarias
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

// Importar el DAO y el modelo
import com.uniajc.dao.EstudianteDao;
import com.uniajc.modelo.Estudiante;

/**
 * Capa de Servicio para la entidad Estudiante.
 * Aquí van las validaciones y las reglas de negocio.
 * El DAO solo guarda y consulta; el Servicio decide si los datos son válidos.
 */
public class EstudianteService {

    // Nombre y apellido: solo letras (con tildes) y espacios, entre 2 y 50 caracteres
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^[\\p{L} ]{2,50}$");

    // Email: formato básico texto@dominio.extension
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    // El Servicio usa el DAO para acceder a la base de datos
    private final EstudianteDao estudianteDao;

    /**
     * Constructor: recibe el DAO que va a utilizar.
     * @param estudianteDao DAO de estudiantes
     */
    public EstudianteService(EstudianteDao estudianteDao) {
        this.estudianteDao = estudianteDao;
    }

    /**
     * Registra un nuevo estudiante después de validar sus datos.
     * @param estudiante Estudiante a registrar
     */
    public void registrarEstudiante(Estudiante estudiante) {
        validarEstudiante(estudiante);
        validarEmailNoRepetido(estudiante);
        estudianteDao.guardar(estudiante);
    }

    /**
     * Obtiene todos los estudiantes.
     * @return Lista de estudiantes
     */
    public List<Estudiante> obtenerTodosLosEstudiantes() {
        return estudianteDao.obtenerTodos();
    }

    /**
     * Busca un estudiante por su ID.
     * @param id Identificador del estudiante
     * @return Optional con el estudiante, vacío si no existe
     */
    public Optional<Estudiante> buscarEstudiantePorId(int id) {
        return estudianteDao.buscarPorId(id);
    }

    /**
     * Actualiza un estudiante existente después de validar sus datos.
     * @param estudiante Estudiante con los datos nuevos
     */
    public void actualizarEstudiante(Estudiante estudiante) {
        if (estudiante == null || estudiante.getIdEstudiante() <= 0) {
            throw new IllegalArgumentException("Estudiante inválido.");
        }

        // Verificar que el estudiante exista antes de actualizar
        Optional<Estudiante> existente = estudianteDao.buscarPorId(estudiante.getIdEstudiante());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Estudiante no encontrado.");
        }

        validarEstudiante(estudiante);
        validarEmailNoRepetido(estudiante);
        estudianteDao.actualizar(estudiante);
    }

    /**
     * Elimina un estudiante por su ID, verificando que exista.
     * @param id Identificador del estudiante
     */
    public void eliminarEstudiante(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido.");
        }

        Optional<Estudiante> existente = estudianteDao.buscarPorId(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("Estudiante no encontrado.");
        }

        estudianteDao.eliminar(id);
    }

    /**
     * Valida que los datos del estudiante cumplan las reglas.
     * @param estudiante Estudiante a validar
     */
    private void validarEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo.");
        }

        // Nombre obligatorio y con formato válido
        if (estudiante.getNombre() == null || estudiante.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (!NOMBRE_PATTERN.matcher(estudiante.getNombre().trim()).matches()) {
            throw new IllegalArgumentException("El nombre solo puede tener letras y espacios (2 a 50 caracteres).");
        }

        // Apellido obligatorio y con formato válido
        if (estudiante.getApellido() == null || estudiante.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        if (!NOMBRE_PATTERN.matcher(estudiante.getApellido().trim()).matches()) {
            throw new IllegalArgumentException("El apellido solo puede tener letras y espacios (2 a 50 caracteres).");
        }

        // Email obligatorio y con formato válido
        if (estudiante.getEmail() == null || estudiante.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }
        if (!EMAIL_PATTERN.matcher(estudiante.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("El formato del email es inválido.");
        }
    }

    /**
     * Regla de negocio: no permitir dos estudiantes con el mismo email.
     * Al actualizar, se ignora el propio estudiante.
     * @param estudiante Estudiante a verificar
     */
    private void validarEmailNoRepetido(Estudiante estudiante) {
        for (Estudiante otro : estudianteDao.obtenerTodos()) {
            boolean mismoEmail = otro.getEmail() != null
                    && otro.getEmail().equalsIgnoreCase(estudiante.getEmail().trim());
            boolean esOtroEstudiante = otro.getIdEstudiante() != estudiante.getIdEstudiante();
            if (mismoEmail && esOtroEstudiante) {
                throw new IllegalArgumentException("Ya existe un estudiante con ese email.");
            }
        }
    }
}