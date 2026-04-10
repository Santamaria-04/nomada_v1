package esic.nomada_v1.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.repository.UsuarioRepository;

@Service
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public AuthenticatedUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadAuthenticatedUserByUsername(username);
    }

    public AuthenticatedUser loadAuthenticatedUserByUsername(String username) {
        Usuario usuario = usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new AuthenticatedUser(usuario);
    }
}
