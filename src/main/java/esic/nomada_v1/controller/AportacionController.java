package esic.nomada_v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import esic.nomada_v1.dto.AportacionDTO;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.service.AportacionService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/aportaciones")
public class AportacionController {

    private final AportacionService aportacionService;

    public AportacionController(AportacionService aportacionService) {
        this.aportacionService = aportacionService;
    }

    @PostMapping
    public ResponseEntity<AportacionDTO> create(@RequestBody AportacionDTO dto,
                                                @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            if (dto != null) {
                dto.setIdUsuario(user.getIdUsuario());
            }
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(aportacionService.save(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<AportacionDTO>> findAll() {
        return ResponseEntity.ok(aportacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AportacionDTO> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(aportacionService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<AportacionDTO>> findByUsuario(@PathVariable Integer idUsuario,
                                                             @AuthenticationPrincipal AuthenticatedUser user) {
        if (!user.isAdmin() && !user.getIdUsuario().equals(idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(aportacionService.findByUsuario(idUsuario));
    }

    @GetMapping("/tema/{idTema}")
    public ResponseEntity<List<AportacionDTO>> findByTema(@PathVariable Integer idTema) {
        return ResponseEntity.ok(aportacionService.findByTema(idTema));
    }

    @GetMapping("/recurso/{idRecurso}")
    public ResponseEntity<List<AportacionDTO>> findByRecurso(@PathVariable Integer idRecurso) {
        return ResponseEntity.ok(aportacionService.findByRecurso(idRecurso));
    }

    @GetMapping("/reportadas")
    public ResponseEntity<List<AportacionDTO>> findReportadas(@AuthenticationPrincipal AuthenticatedUser user) {
        if (!user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(aportacionService.findReportadas());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AportacionDTO> update(@PathVariable Integer id,
                                                @RequestBody AportacionDTO dto,
                                                @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            if (dto != null) {
                dto.setIdUsuario(user.getIdUsuario());
            }
            return ResponseEntity.ok(aportacionService.update(id, dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id,
                                       @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            aportacionService.delete(id, user.getIdUsuario(), user.isAdmin());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
