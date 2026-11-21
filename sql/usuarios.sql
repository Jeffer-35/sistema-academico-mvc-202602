-- ==========================================================
-- Tabla de usuarios para el login del Sistema Académico
-- Las contraseñas NUNCA se guardan en texto plano:
-- se guarda su hash SHA-512 (128 caracteres hexadecimales).
-- ==========================================================

CREATE TABLE IF NOT EXISTS "practica-mvc".usuarios (
    id_usuario      SERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(128) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    rol             VARCHAR(20)  NOT NULL DEFAULT 'ADMINISTRADOR'
                    CHECK (rol IN ('ADMINISTRADOR', 'DOCENTE', 'ESTUDIANTE')),
    activo          BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Usuarios de prueba. La contraseña va entre comillas y PostgreSQL la convierte a SHA-512
-- (Java hace exactamente lo mismo al iniciar sesión, por eso coinciden).
INSERT INTO "practica-mvc".usuarios (username, password_hash, nombre_completo, rol) VALUES
('admin',     encode(sha512(convert_to('Admin2026*', 'UTF8')), 'hex'),     'Administrador',    'ADMINISTRADOR'),
('jefferson', encode(sha512(convert_to('Jefferson2026*', 'UTF8')), 'hex'), 'Jefferson Paredes', 'ADMINISTRADOR'),
('deibit',    encode(sha512(convert_to('Deibit2026*', 'UTF8')), 'hex'),    'Deibit Urbano',    'ADMINISTRADOR')
ON CONFLICT (username) DO NOTHING;

-- Para comprobar: la contraseña no se ve, solo el hash
SELECT id_usuario, username, nombre_completo, rol, activo, LEFT(password_hash, 20) || '...' AS hash
FROM "practica-mvc".usuarios
ORDER BY id_usuario;
