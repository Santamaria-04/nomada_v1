package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.AuthResponseDTO;
import esic.nomada_v1.dto.LoginRequestDTO;
import esic.nomada_v1.dto.UsuarioDTO;
import esic.nomada_v1.model.Usuario;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.FavoritoRepository;
import esic.nomada_v1.repository.HistorialRepository;
import esic.nomada_v1.repository.ReporteRepository;
import esic.nomada_v1.repository.UsuarioRepository;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.security.JwtService;
import esic.nomada_v1.util.PasswordUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final FabricaUsuarioService fabricaUsuario;
    private final JwtService jwtService;
    private final AportacionRepository aportacionRepository;
    private final FavoritoRepository favoritoRepository;
    private final HistorialRepository historialRepository;
    private final ReporteRepository reporteRepository;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          FabricaUsuarioService fabricaUsuario,
                          JwtService jwtService,
                          AportacionRepository aportacionRepository,
                          FavoritoRepository favoritoRepository,
                          HistorialRepository historialRepository,
                          ReporteRepository reporteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.fabricaUsuario = fabricaUsuario;
        this.jwtService = jwtService;
        this.aportacionRepository = aportacionRepository;
        this.favoritoRepository = favoritoRepository;
        this.historialRepository = historialRepository;
        this.reporteRepository = reporteRepository;
    }

    @Transactional
    public UsuarioDTO register(UsuarioDTO dto) {
        validateRegistration(dto);
        String normalizedEmail = normalizeEmail(dto.getEmail());

        if (usuarioRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = fabricaUsuario.createUsuario(dto);
        usuario.setEmail(normalizedEmail);
        usuario.setPassword(PasswordUtils.hash(dto.getPassword()));
        usuario.setNombre(dto.getNombre().trim());
        usuario.setImagenPerfil(normalizeOptionalText(dto.getImagenPerfil()));
        usuario.setRol("USER");

        usuario.setFechaRegistro(LocalDateTime.now());

        Usuario guardado = usuarioRepository.save(usuario);

        return fabricaUsuario.createUsuarioDTO(guardado);
    }

    public UsuarioDTO save(UsuarioDTO dto) {
        return register(dto);
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto) {
        if (dto == null || isBlank(dto.getEmail()) || isBlank(dto.getPassword())) {
            throw new IllegalArgumentException("Email y password son obligatorios");
        }

        Usuario usuario = usuarioRepository.findByEmail(normalizeEmail(dto.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales incorrectas"));

        if (!PasswordUtils.matches(dto.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales incorrectas");
        }

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(usuario);
        return new AuthResponseDTO(
                jwtService.generateToken(authenticatedUser),
                fabricaUsuario.createUsuarioDTO(usuario)
        );
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll()
                .stream()
                .map(fabricaUsuario::createUsuarioDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO findById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return fabricaUsuario.createUsuarioDTO(usuario);
    }

    @Transactional
    public UsuarioDTO update(Integer id, UsuarioDTO dto, boolean allowRoleChange) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        validateUpdate(dto);
        String normalizedEmail = normalizeEmail(dto.getEmail());

        if (usuarioRepository.existsByEmailAndIdUsuarioNot(normalizedEmail, id)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        usuario.setNombre(dto.getNombre().trim());
        usuario.setEmail(normalizedEmail);
        usuario.setImagenPerfil(normalizeOptionalText(dto.getImagenPerfil()));

        if (!isBlank(dto.getPassword())) {
            usuario.setPassword(PasswordUtils.hash(dto.getPassword()));
        }

        if (allowRoleChange && !isBlank(dto.getRol())) {
            usuario.setRol(normalizeRole(dto.getRol()));
        }

        Usuario guardado = usuarioRepository.save(usuario);
        return fabricaUsuario.createUsuarioDTO(guardado);
    }

    @Transactional
    public void delete(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Usuario no encontrado");
        }
        if (aportacionRepository.existsByUsuario_IdUsuario(id)) {
            throw new IllegalArgumentException("No se puede eliminar un usuario con aportaciones asociadas");
        }
        if (favoritoRepository.existsByUsuario_IdUsuario(id)) {
            throw new IllegalArgumentException("No se puede eliminar un usuario con favoritos asociados");
        }
        if (historialRepository.existsByUsuario_IdUsuario(id)) {
            throw new IllegalArgumentException("No se puede eliminar un usuario con historial asociado");
        }
        if (reporteRepository.existsByUsuarioReporta_IdUsuario(id)) {
            throw new IllegalArgumentException("No se puede eliminar un usuario con reportes asociados");
        }
        usuarioRepository.deleteById(id);
    }

    private void validateRegistration(UsuarioDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        validateNameAndEmail(dto);
        if (isBlank(dto.getPassword()) || dto.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (!isBlank(dto.getRol())) {
            normalizeRole(dto.getRol());
        }
    }

    private void validateUpdate(UsuarioDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        validateNameAndEmail(dto);
        if (!isBlank(dto.getPassword()) && dto.getPassword().trim().length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
        }
        if (!isBlank(dto.getRol())) {
            normalizeRole(dto.getRol());
        }
    }

    private void validateNameAndEmail(UsuarioDTO dto) {
        if (isBlank(dto.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (isBlank(dto.getEmail())) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        String normalizedEmail = normalizeEmail(dto.getEmail());
        if (!normalizedEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeRole(String role) {
        String normalizedRole = role.trim().toUpperCase(Locale.ROOT);
        if (!normalizedRole.equals("USER") && !normalizedRole.equals("ADMIN")) {
            throw new IllegalArgumentException("El rol debe ser USER o ADMIN");
        }
        return normalizedRole;
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
