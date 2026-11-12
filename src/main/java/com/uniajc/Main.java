package com.uniajc;

import javax.swing.SwingUtilities;

// Importar todas las capas del sistema
import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.EstudianteDao;
import com.uniajc.servicios.DocenteService;
import com.uniajc.servicios.EstudianteService;
import com.uniajc.vista.VistaPrincipalSwing;

/**
 * Punto de entrada del sistema.
 * Crea y conecta las capas del MVC para cada entidad:
 * DAO -> Servicio -> Controlador -> Vista
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Bienvenido al Sistema Académico!");

        // Cadena de Estudiante: DAO -> Servicio -> Controlador
        EstudianteDao estudianteDao = new EstudianteDao();
        EstudianteService estudianteService = new EstudianteService(estudianteDao);
        ControladorEstudiante controladorEstudiante = new ControladorEstudiante(estudianteService);

        // Cadena de Docente: DAO -> Servicio -> Controlador
        DocenteDao docenteDao = new DocenteDao();
        DocenteService docenteService = new DocenteService(docenteDao);
        ControladorDocente controladorDocente = new ControladorDocente(docenteService);

        // La ventana principal recibe los dos controladores
        SwingUtilities.invokeLater(() ->
                new VistaPrincipalSwing(controladorEstudiante, controladorDocente).setVisible(true));
    }
}