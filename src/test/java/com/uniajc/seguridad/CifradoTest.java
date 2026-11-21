package com.uniajc.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias de la utilidad de cifrado SHA-512.
 */
class CifradoTest {

    @Test
    @DisplayName("SHA-512 de 'abc' da el valor oficial del estándar")
    void sha512ValorConocido() {
        // Valor de referencia publicado en el estándar FIPS 180-2
        String esperado = "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a"
                + "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f";

        assertEquals(esperado, Cifrado.sha512("abc"));
    }

    @Test
    @DisplayName("El hash siempre tiene 128 caracteres")
    void hashDe128Caracteres() {
        assertEquals(128, Cifrado.sha512("Admin2026*").length());
    }

    @Test
    @DisplayName("Dos contraseñas distintas dan hashes distintos")
    void contrasenasDistintas() {
        assertTrue(!Cifrado.sha512("Admin2026*").equals(Cifrado.sha512("admin2026*")));
    }

    @Test
    @DisplayName("coincide() reconoce la contraseña correcta y rechaza la incorrecta")
    void coincideConElHash() {
        String hashGuardado = Cifrado.sha512("Admin2026*");

        assertTrue(Cifrado.coincide("Admin2026*", hashGuardado));
        assertTrue(!Cifrado.coincide("otraClave", hashGuardado));
    }
}
