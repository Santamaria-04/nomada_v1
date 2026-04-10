package esic.nomada_v1.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import esic.nomada_v1.model.Usuario;

import java.util.Collection;
import java.util.List;

public class AuthenticatedUser implements UserDetails {

    private final Integer idUsuario;
    private final String nombre;
    private final String email;
    private final String password;
    private final String rol;
    private final List<GrantedAuthority> authorities;

    public AuthenticatedUser(Usuario usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.email = usuario.getEmail();
        this.password = usuario.getPassword();
        this.rol = usuario.getRol();
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol()));
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getRol() {
        return rol;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(rol);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
