package esic.nomada_v1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import esic.nomada_v1.dto.ReporteDTO;
import esic.nomada_v1.dto.ResolverReporteDTO;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.service.ReporteService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostMapping
    public ResponseEntity<ReporteDTO> create(@RequestBody ReporteDTO dto,
                                             @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(reporteService.create(user.getIdUsuario(), dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/mis-reportes")
    public ResponseEntity<List<ReporteDTO>> findMine(@AuthenticationPrincipal AuthenticatedUser user) {
        return ResponseEntity.ok(reporteService.findByUsuario(user.getIdUsuario()));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<ReporteDTO>> findPendientes(@AuthenticationPrincipal AuthenticatedUser user) {
        if (!user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(reporteService.findPendientes());
    }

    @GetMapping("/{idReporte}")
    public ResponseEntity<ReporteDTO> findById(@PathVariable Integer idReporte,
                                               @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            ReporteDTO reporte = reporteService.findById(idReporte);
            if (!user.isAdmin() && !user.getIdUsuario().equals(reporte.getIdUsuarioReporta())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(reporte);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{idReporte}/resolver")
    public ResponseEntity<ReporteDTO> resolver(@PathVariable Integer idReporte,
                                               @RequestBody ResolverReporteDTO dto,
                                               @AuthenticationPrincipal AuthenticatedUser user) {
        if (!user.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            return ResponseEntity.ok(reporteService.resolver(idReporte, dto.getAccion()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
