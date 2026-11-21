package com.uniajc.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.uniajc.dao.UsuarioDao;
import com.uniajc.modelo.Usuario;
import com.uniajc.seguridad.Cifrado;

/**
 * Pruebas unitarias del inicio de sesión (UsuarioService).
 * Usa un DAO falso en memoria para no tocar la base de datos real.
 */
class UsuarioServiceTest {

    private UsuarioService servicio;

    @BeforeEach
    void prepararServicio() {
        UsuarioDaoFalso dao = new UsuarioDaoFalso();
        // En la "base de datos" se guarda el hash, nunca la contraseña real
        dao.lista.add(new Usuario(1, "admin", Cifrado.sha512("Admin2026*"), "Administrador", "ADMINISTRADOR", true));
        dao.lista.add(new Usuario(2, "inactivo", Cifrado.sha512("Clave123*"), "Usuario Inactivo", "DOCENTE", false));
        servicio = new UsuarioService(dao);
    }

    @Test
    @DisplayName("Con usuario y contraseña correctos inicia sesión")
    void loginCorrecto() {
        Usuario usuario = servicio.iniciarSesion("admin", "Admin2026*");

        assertEquals("Administrador", usuario.getNombreCompleto());
    }

    @Test
    @DisplayName("El nombre de usuario no distingue mayúsculas")
    void usuarioSinImportarMayusculas() {
        Usuario usuario = servicio.iniciarSesion("  ADMIN ", "Admin2026*");

        assertEquals("admin", usuario.getUsername());
    }

    @Test
    @DisplayName("Con contraseña incorrecta no deja entrar")
    void contrasenaIncorrecta() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.iniciarSesion("admin", "admin2026*"));

        assertEquals("Usuario o contraseña incorrectos.", error.getMessage());
    }

    @Test
    @DisplayName("Si el usuario no existe da el MISMO mensaje (no da pistas)")
    void usuarioInexistente() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.iniciarSesion("nadie", "Admin2026*"));

        assertEquals("Usuario o contraseña incorrectos.", error.getMessage());
    }

    @Test
    @DisplayName("Un usuario inactivo no puede entrar")
    void usuarioInactivo() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.iniciarSesion("inactivo", "Clave123*"));

        assertEquals("El usuario está inactivo. Comuníquese con el administrador.", error.getMessage());
    }

    @Test
    @DisplayName("No permite campos vacíos")
    void camposVacios() {
        assertThrows(IllegalArgumentException.class, () -> servicio.iniciarSesion("", "Admin2026*"));
        assertThrows(IllegalArgumentException.class, () -> servicio.iniciarSesion("admin", ""));
    }

    // ===================== DAO FALSO =====================

    private static class UsuarioDaoFalso extends UsuarioDao {

        private final List<Usuario> lista = new ArrayList<>();

        @Override
        public Optional<Usuario> buscarPorUsername(String username) {
            return lista.stream()
                    .filter(u -> u.getUsername().equalsIgnoreCase(username))
                    .findFirst();
        }
    }
}
