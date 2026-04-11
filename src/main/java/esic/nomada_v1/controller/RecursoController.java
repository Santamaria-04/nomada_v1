package esic.nomada_v1.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.security.AuthenticatedUser;
import esic.nomada_v1.service.RecursoService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/recursos")
public class RecursoController {

    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) {
        this.recursoService = recursoService;
    }

    @GetMapping
    public ResponseEntity<List<RecursoDTO>> getAll() {
        return ResponseEntity.ok(recursoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoDTO> getById(@PathVariable Integer id,
                                              @AuthenticationPrincipal AuthenticatedUser user) {
        try {
            Integer idUsuario = user != null ? user.getIdUsuario() : null;
            return ResponseEntity.ok(recursoService.findById(id, idUsuario));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<RecursoDTO> create(@RequestBody RecursoDTO dto) {
        try {
            if (dto != null) {
                dto.setIdRecurso(null);
            }
            RecursoDTO creado = recursoService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecursoDTO> update(@PathVariable Integer id,
                                             @RequestBody RecursoDTO dto) {
        try {
            recursoService.findById(id, null);
            if (dto != null) {
                dto.setIdRecurso(id);
            }
            RecursoDTO actualizado = recursoService.save(dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            recursoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
