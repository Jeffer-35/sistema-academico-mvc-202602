package com.uniajc;

import javax.swing.SwingUtilities;

// Importar todas las capas del sistema
import com.uniajc.controlador.ControladorDocente;
import com.uniajc.controlador.ControladorEstudiante;
import com.uniajc.controlador.ControladorGrupo;
import com.uniajc.controlador.ControladorInscripcion;
import com.uniajc.controlador.ControladorMateria;
import com.uniajc.controlador.ControladorUsuario;
import com.uniajc.dao.DocenteDao;
import com.uniajc.dao.EstudianteDao;
import com.uniajc.dao.GrupoDao;
import com.uniajc.dao.InscripcionDao;
import com.uniajc.dao.MateriaDao;
import com.uniajc.dao.UsuarioDao;
import com.uniajc.db.DatabaseConexion;
import com.uniajc.modelo.Usuario;
import com.uniajc.servicios.DocenteService;
import com.uniajc.servicios.EstudianteService;
import com.uniajc.servicios.GrupoService;
import com.uniajc.servicios.InscripcionService;
import com.uniajc.servicios.MateriaService;
import com.uniajc.servicios.UsuarioService;
import com.uniajc.vista.VistaLoginSwing;
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

        // Cadena de Materia: DAO -> Servicio -> Controlador
        MateriaDao materiaDao = new MateriaDao();
        MateriaService materiaService = new MateriaService(materiaDao);
        ControladorMateria controladorMateria = new ControladorMateria(materiaService);

        // Cadena de Grupo: necesita también los DAO de Materia y Docente
        // para validar que existan (llaves foráneas)
        GrupoDao grupoDao = new GrupoDao();
        GrupoService grupoService = new GrupoService(grupoDao, materiaDao, docenteDao);
        ControladorGrupo controladorGrupo = new ControladorGrupo(grupoService);

        // Cadena de Inscripción: necesita también los DAO de Estudiante y Grupo
        // para validar que existan (llaves foráneas)
        InscripcionDao inscripcionDao = new InscripcionDao();
        InscripcionService inscripcionService = new InscripcionService(inscripcionDao, estudianteDao, grupoDao);
        ControladorInscripcion controladorInscripcion = new ControladorInscripcion(inscripcionService);

        // Cadena de Usuario (login): DAO -> Servicio -> Controlador
        UsuarioDao usuarioDao = new UsuarioDao();
        UsuarioService usuarioService = new UsuarioService(usuarioDao);
        ControladorUsuario controladorUsuario = new ControladorUsuario(usuarioService);

        SwingUtilities.invokeLater(() -> {
            // 1. Primero se muestra el login. Es modal: el programa espera aquí.
            VistaLoginSwing login = new VistaLoginSwing(controladorUsuario);
            login.setVisible(true);

            // 2. Si cerró el login sin entrar, se cierra todo
            Usuario usuario = login.getUsuarioAutenticado();
            if (usuario == null) {
                DatabaseConexion.closeConnection();
                System.exit(0);
                return;
            }

            // 3. Si entró, se abre la ventana principal con los cinco controladores
            VistaPrincipalSwing ventana = new VistaPrincipalSwing(controladorEstudiante, controladorDocente,
                    controladorMateria, controladorGrupo, controladorInscripcion);
            ventana.setTitle(ventana.getTitle() + "  |  " + usuario.getNombreCompleto()
                    + " (" + usuario.getRol() + ")");
            ventana.setVisible(true);
        });
    }
}
