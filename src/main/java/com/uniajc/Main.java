package com.uniajc;

import javax.swing.SwingUtilities;

// Importar todas las capas del sistema
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.dao.EstudianteDao;
import com.uniajc.servicios.EstudianteService;
import com.uniajc.vista.VistaPrincipalSwing;

/**
 * Punto de entrada del sistema.
 * Crea y conecta las capas del MVC: DAO -> Servicio -> Controlador -> Vista
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenido al Sistema Académico!");

        // 1. DAO (acceso a la base de datos)
        EstudianteDao estudianteDao = new EstudianteDao();

        // 2. Servicio (validaciones), recibe el DAO
        EstudianteService estudianteService = new EstudianteService(estudianteDao);

        // 3. Controlador, recibe el Servicio
        ControladorEstudiante controlador = new ControladorEstudiante(estudianteService);

        // 4. Vista Swing: se abre la ventana principal
        // (Si quieres usar la vista de consola, cambia esta línea por:
        //  new com.uniajc.vista.VistaEstudiante(controlador).mostrarMenu();)
        SwingUtilities.invokeLater(() -> new VistaPrincipalSwing(controlador).setVisible(true));
    }
}