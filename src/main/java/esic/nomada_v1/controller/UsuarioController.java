package esic.nomada_v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import esic.nomada_v1.dto.AuthResponseDTO;
import esic.nomada_v1.dto.LoginRequestDTO;
import esic.nomada_v1.dto.UsuarioDTO;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.service.UsuarioService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping({"", "/registro"})
    public ResponseEntity<UsuarioDTO> register(@RequestBody UsuarioDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(usuarioService.register(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        try {
            return ResponseEntity.ok(usuarioService.login(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> findById(@PathVariable Integer id,
                                               @AuthenticationPrincipal AuthenticatedUser user) {
        if (!canAccessUser(user, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(usuarioService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> update(@PathVariable Integer id,
                                             @RequestBody UsuarioDTO dto,
                                             @AuthenticationPrincipal AuthenticatedUser user) {
        if (!canAccessUser(user, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(usuarioService.update(id, dto, user.isAdmin()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id,
                                       @AuthenticationPrincipal AuthenticatedUser user) {
        if (!canAccessUser(user, id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            usuarioService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean canAccessUser(AuthenticatedUser user, Integer targetUserId) {
        return user != null && (user.isAdmin() || user.getIdUsuario().equals(targetUserId));
    }
}
