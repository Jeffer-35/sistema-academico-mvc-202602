package com.uniajc.controlador;

// Importar el modelo y el servicio
import com.uniajc.modelo.Usuario;
import com.uniajc.servicios.UsuarioService;

/**
 * Controlador del inicio de sesión.
 * Recibe el usuario y la contraseña de la vista y se los pasa al Servicio.
 * El controlador NO valida ni cifra; eso lo hace el Servicio.
 */
public class ControladorUsuario {

    private final UsuarioService usuarioService;

    /**
     * Constructor: recibe el Servicio que va a utilizar.
     * @param usuarioService Servicio de usuarios
     */
    public ControladorUsuario(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * Intenta iniciar sesión.
     * La contraseña llega como char[] (así la entrega JPasswordField)
     * y se convierte a String solo para validarla.
     */
    public Usuario iniciarSesion(String username, char[] contrasena) {
        return usuarioService.iniciarSesion(username, new String(contrasena));
    }
}
