package esic.nomada_v1.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TextValidationUtilsTest {

    @Test
    void shouldDetectExternalLinks() {
        assertTrue(TextValidationUtils.containsExternalLink("Mira https://example.com"));
        assertTrue(TextValidationUtils.containsExternalLink("Mira www.example.com"));
        assertFalse(TextValidationUtils.containsExternalLink("Texto sin enlaces"));
    }
}
