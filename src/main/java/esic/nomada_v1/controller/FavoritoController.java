package esic.nomada_v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import esic.nomada_v1.dto.FavoritoDTO;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.service.FavoritoService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    private final FavoritoService favoritoService;

    public FavoritoController(FavoritoService favoritoService) {
        this.favoritoService = favoritoService;
    }

    @PostMapping
    public ResponseEntity<FavoritoDTO> addFavorito(@RequestBody FavoritoDTO dto,
                                                   @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            dto.setIdUsuario(user.getIdUsuario());
            FavoritoDTO creado = favoritoService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<FavoritoDTO>> getByUsuario(@PathVariable Integer idUsuario,
                                                          @AuthenticationPrincipal AuthenticatedUser user) {
        if (!user.isAdmin() && !user.getIdUsuario().equals(idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(favoritoService.findByUsuario(idUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id,
                                       @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            favoritoService.delete(id, user.getIdUsuario(), user.isAdmin());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
