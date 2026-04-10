package esic.nomada_v1.security;

import org.junit.jupiter.api.Test;
import esic.nomada_v1.model.Usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void shouldGenerateAndValidateToken() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(7);
        usuario.setNombre("Pablo");
        usuario.setEmail("pablo@example.com");
        usuario.setPassword("hashed");
        usuario.setRol("USER");

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(usuario);
        JwtService jwtService = new JwtService("secret-test-jwt", 60000);

        String token = jwtService.generateToken(authenticatedUser);
        JwtClaims claims = jwtService.parseToken(token);

        assertEquals(7, claims.userId());
        assertEquals("pablo@example.com", claims.email());
        assertEquals("USER", claims.role());
        assertTrue(jwtService.isTokenValid(token, authenticatedUser));
    }
}
