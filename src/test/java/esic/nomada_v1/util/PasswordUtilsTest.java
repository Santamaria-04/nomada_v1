package esic.nomada_v1.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {

    @Test
    void shouldHashAndValidatePasswords() {
        String rawPassword = "nomada123";

        String hashedPassword = PasswordUtils.hash(rawPassword);

        assertNotEquals(rawPassword, hashedPassword);
        assertTrue(PasswordUtils.matches(rawPassword, hashedPassword));
        assertFalse(PasswordUtils.matches("otraPassword", hashedPassword));
    }
}
