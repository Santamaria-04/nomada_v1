package esic.nomada_v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import esic.nomada_v1.dto.HistorialDTO;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.service.HistorialService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<HistorialDTO>> findByUsuario(@PathVariable Integer idUsuario,
                                                            @AuthenticationPrincipal AuthenticatedUser user) {
        if (!user.isAdmin() && !user.getIdUsuario().equals(idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(historialService.findByUsuario(idUsuario));
    }

    @DeleteMapping("/{idHistorial}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer idHistorial,
                                           @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            historialService.deleteById(idHistorial, user.getIdUsuario(), user.isAdmin());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> deleteAllByUsuario(@PathVariable Integer idUsuario,
                                                   @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            historialService.deleteAllByUsuario(idUsuario, user.getIdUsuario(), user.isAdmin());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
