package esic.nomada_v1.security;

import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.repository.UsuarioRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;

class JwtAuthenticationFilterTest {

    @Test
    void shouldIgnoreTokenWhenUserNoLongerExists() throws ServletException, IOException {
        JwtService jwtService = new JwtService("secret-test-jwt", 60000);
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(7);
        usuario.setNombre("Pablo");
        usuario.setEmail("pablo@example.com");
        usuario.setPassword("hashed");
        usuario.setRol("USER");

        String token = jwtService.generateToken(new AuthenticatedUser(usuario));
        UsuarioRepository usuarioRepository = emptyUsuarioRepository();
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService,
                new AuthenticatedUserDetailsService(usuarioRepository)
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        assertDoesNotThrow(() -> filter.doFilter(request, response, chain));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private UsuarioRepository emptyUsuarioRepository() {
        return (UsuarioRepository) Proxy.newProxyInstance(
                UsuarioRepository.class.getClassLoader(),
                new Class<?>[]{UsuarioRepository.class},
                (proxy, method, args) -> {
                    if ("findByEmail".equals(method.getName())) {
                        return Optional.empty();
                    }
                    throw new UnsupportedOperationException("Metodo no esperado en test: " + method.getName());
                }
        );
    }
}
