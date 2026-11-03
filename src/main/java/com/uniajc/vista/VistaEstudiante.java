package com.uniajc.vista;

// Importaciones necesarias
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

// Importar el controlador y el modelo
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.modelo.Estudiante;

/**
 * Vista de consola para la entidad Estudiante.
 * Muestra un menú, pide los datos con Scanner
 * y le pasa todo al Controlador.
 * La vista NO valida ni guarda; solo muestra y captura datos.
 */
public class VistaEstudiante {

    private final ControladorEstudiante controlador;
    private final Scanner scanner;

    /**
     * Constructor: recibe el controlador que va a utilizar.
     * @param controlador Controlador de estudiantes
     */
    public VistaEstudiante(ControladorEstudiante controlador) {
        this.controlador = controlador;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Muestra el menú principal hasta que el usuario elija salir.
     */
    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n===== GESTIÓN DE ESTUDIANTES =====");
            System.out.println("1. Registrar estudiante");
            System.out.println("2. Listar estudiantes");
            System.out.println("3. Buscar estudiante por ID");
            System.out.println("4. Actualizar estudiante");
            System.out.println("5. Eliminar estudiante");
            System.out.println("0. Salir");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> registrar();
                case 2 -> listar();
                case 3 -> buscar();
                case 4 -> actualizar();
                case 5 -> eliminar();
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 0);
    }

    /**
     * Pide los datos y registra un estudiante nuevo.
     */
    private void registrar() {
        System.out.println("\n--- Registrar estudiante ---");
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        try {
            controlador.registrarEstudiante(nombre, apellido, email);
            System.out.println("Estudiante registrado con éxito.");
        } catch (IllegalArgumentException e) {
            // Errores de validación que lanza el Servicio
            System.out.println("Dato inválido: " + e.getMessage());
        } catch (RuntimeException e) {
            // Errores de la base de datos que lanza el DAO
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Muestra todos los estudiantes registrados.
     */
    private void listar() {
        System.out.println("\n--- Lista de estudiantes ---");
        try {
            List<Estudiante> estudiantes = controlador.listarEstudiantes();
            if (estudiantes.isEmpty()) {
                System.out.println("No hay estudiantes registrados.");
                return;
            }
            for (Estudiante e : estudiantes) {
                mostrarEstudiante(e);
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Busca un estudiante por su ID y lo muestra.
     */
    private void buscar() {
        System.out.println("\n--- Buscar estudiante ---");
        int id = leerEntero("ID del estudiante: ");
        try {
            Optional<Estudiante> encontrado = controlador.buscarEstudiantePorId(id);
            if (encontrado.isPresent()) {
                mostrarEstudiante(encontrado.get());
            } else {
                System.out.println("No existe un estudiante con ese ID.");
            }
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Pide el ID y los datos nuevos para actualizar un estudiante.
     * Si el usuario deja un campo vacío, se conserva el valor actual.
     */
    private void actualizar() {
        System.out.println("\n--- Actualizar estudiante ---");
        int id = leerEntero("ID del estudiante: ");
        try {
            Optional<Estudiante> encontrado = controlador.buscarEstudiantePorId(id);
            if (encontrado.isEmpty()) {
                System.out.println("No existe un estudiante con ese ID.");
                return;
            }
            Estudiante actual = encontrado.get();
            System.out.println("Datos actuales:");
            mostrarEstudiante(actual);
            System.out.println("(Presione Enter para dejar el dato igual)");

            System.out.print("Nuevo nombre: ");
            String nombre = scanner.nextLine();
            if (nombre.isBlank()) {
                nombre = actual.getNombre();
            }

            System.out.print("Nuevo apellido: ");
            String apellido = scanner.nextLine();
            if (apellido.isBlank()) {
                apellido = actual.getApellido();
            }

            System.out.print("Nuevo email: ");
            String email = scanner.nextLine();
            if (email.isBlank()) {
                email = actual.getEmail();
            }

            controlador.actualizarEstudiante(id, nombre, apellido, email);
            System.out.println("Estudiante actualizado con éxito.");
        } catch (IllegalArgumentException e) {
            System.out.println("Dato inválido: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Pide el ID, confirma y elimina un estudiante.
     */
    private void eliminar() {
        System.out.println("\n--- Eliminar estudiante ---");
        int id = leerEntero("ID del estudiante: ");
        System.out.print("¿Seguro que desea eliminarlo? (s/n): ");
        String confirmacion = scanner.nextLine();

        if (!confirmacion.equalsIgnoreCase("s")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            controlador.eliminarEstudiante(id);
            System.out.println("Estudiante eliminado con éxito.");
        } catch (IllegalArgumentException e) {
            System.out.println("Dato inválido: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Muestra los datos de un estudiante en una línea.
     */
    private void mostrarEstudiante(Estudiante e) {
        System.out.println("ID: " + e.getIdEstudiante()
                + " | Nombre: " + e.getNombre()
                + " | Apellido: " + e.getApellido()
                + " | Email: " + e.getEmail());
    }

    /**
     * Lee un número entero. Si el usuario escribe letras, vuelve a pedirlo.
     */
    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = scanner.nextLine();
            try {
                return Integer.parseInt(texto.trim());
            } catch (NumberFormatException e) {
                System.out.println("Debe escribir un número.");
            }
        }
    }
}