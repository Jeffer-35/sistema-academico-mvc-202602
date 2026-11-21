package com.uniajc.seguridad;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad para cifrar contraseñas con SHA-512.
 *
 * SHA-512 es una función hash: convierte cualquier texto en un código de
 * 128 caracteres hexadecimales y NO se puede devolver al texto original.
 * Por eso en la base de datos se guarda el hash y nunca la contraseña real.
 *
 * Se usa SHA-512 y no MD5 porque MD5 ya no se considera seguro para contraseñas.
 */
public final class Cifrado {

    // Constructor privado: es una clase de utilidad, no se crean objetos
    private Cifrado() {
    }

    /**
     * Calcula el hash SHA-512 de un texto.
     * Devuelve el mismo resultado que en PostgreSQL:
     * encode(sha512(convert_to(texto, 'UTF8')), 'hex')
     *
     * @param texto Texto a cifrar (por ejemplo, la contraseña)
     * @return Hash en hexadecimal, en minúsculas (128 caracteres)
     */
    public static String sha512(String texto) {
        if (texto == null) {
            throw new IllegalArgumentException("El texto a cifrar no puede ser nulo.");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] bytes = digest.digest(texto.getBytes(StandardCharsets.UTF_8));

            // Convertir cada byte a dos caracteres hexadecimales
            StringBuilder hex = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // Java siempre trae SHA-512, así que esto no debería pasar
            throw new IllegalStateException("SHA-512 no está disponible.", e);
        }
    }

    /**
     * Compara una contraseña escrita con un hash guardado.
     * Usa MessageDigest.isEqual, que compara en tiempo constante
     * (no da pistas por cuánto se demora en responder).
     *
     * @param contrasena Contraseña escrita por el usuario
     * @param hashGuardado Hash que está en la base de datos
     * @return true si coinciden
     */
    public static boolean coincide(String contrasena, String hashGuardado) {
        if (contrasena == null || hashGuardado == null) {
            return false;
        }
        byte[] calculado = sha512(contrasena).getBytes(StandardCharsets.UTF_8);
        byte[] guardado = hashGuardado.trim().toLowerCase().getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(calculado, guardado);
    }
}
