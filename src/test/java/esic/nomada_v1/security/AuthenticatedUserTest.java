package esic.nomada_v1.security;

import org.junit.jupiter.api.Test;
import esic.nomada_v1.model.Usuario;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticatedUserTest {

    @Test
    void shouldIdentifyAdminRole() {
        Usuario admin = new Usuario();
        admin.setIdUsuario(1);
        admin.setNombre("Admin");
        admin.setEmail("admin@example.com");
        admin.setPassword("hash");
        admin.setRol("ADMIN");

        Usuario user = new Usuario();
        user.setIdUsuario(2);
        user.setNombre("User");
        user.setEmail("user@example.com");
        user.setPassword("hash");
        user.setRol("USER");

        assertTrue(new AuthenticatedUser(admin).isAdmin());
        assertFalse(new AuthenticatedUser(user).isAdmin());
    }
}
