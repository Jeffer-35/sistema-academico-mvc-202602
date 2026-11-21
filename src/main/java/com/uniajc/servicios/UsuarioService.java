package com.uniajc.servicios;

// Importaciones necesarias
import java.util.Optional;

// Importar el DAO, el modelo y la utilidad de cifrado
import com.uniajc.dao.UsuarioDao;
import com.uniajc.modelo.Usuario;
import com.uniajc.seguridad.Cifrado;

/**
 * Capa de Servicio para el inicio de sesión.
 * Reglas:
 *  - Usuario y contraseña son obligatorios.
 *  - La contraseña escrita se cifra con SHA-512 y se compara con el hash guardado.
 *  - Si el usuario no existe o la contraseña es incorrecta, se da el MISMO mensaje,
 *    para no darle pistas a alguien que intente adivinar usuarios.
 *  - Un usuario inactivo no puede entrar.
 */
public class UsuarioService {

    private static final String MENSAJE_CREDENCIALES = "Usuario o contraseña incorrectos.";

    private final UsuarioDao usuarioDao;

    /**
     * Constructor: recibe el DAO que va a utilizar.
     * @param usuarioDao DAO de usuarios
     */
    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    /**
     * Valida el usuario y la contraseña.
     * @param username Nombre de usuario escrito
     * @param contrasena Contraseña escrita (en texto plano, solo vive en memoria)
     * @return El usuario autenticado
     * @throws IllegalArgumentException si los datos son incorrectos
     */
    public Usuario iniciarSesion(String username, String contrasena) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Escriba el nombre de usuario.");
        }
        if (contrasena == null || contrasena.isEmpty()) {
            throw new IllegalArgumentException("Escriba la contraseña.");
        }

        Optional<Usuario> encontrado = usuarioDao.buscarPorUsername(username.trim());

        if (encontrado.isEmpty() || !Cifrado.coincide(contrasena, encontrado.get().getPasswordHash())) {
            throw new IllegalArgumentException(MENSAJE_CREDENCIALES);
        }

        Usuario usuario = encontrado.get();
        if (!usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario está inactivo. Comuníquese con el administrador.");
        }
        return usuario;
    }
}
