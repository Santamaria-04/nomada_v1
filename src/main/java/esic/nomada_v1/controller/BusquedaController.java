package esic.nomada_v1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import esic.nomada_v1.dto.BusquedaResponseDTO;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.service.BusquedaService;

@RestController
@RequestMapping("/api/busquedas")
public class BusquedaController {

    private final BusquedaService busquedaService;

    public BusquedaController(BusquedaService busquedaService) {
        this.busquedaService = busquedaService;
    }

    @GetMapping
    public ResponseEntity<BusquedaResponseDTO> search(@RequestParam String termino,
                                                      @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            return ResponseEntity.ok(busquedaService.search(user.getIdUsuario(), termino));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
