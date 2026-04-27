package esic.nomada_v1.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import esic.nomada_v1.dto.TemaDTO;
import esic.nomada_v1.service.TemaService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/temas")
public class TemaController {

    private final TemaService temaService;

    public TemaController(TemaService temaService) {
        this.temaService = temaService;
    }

    @GetMapping
    public ResponseEntity<List<TemaDTO>> getAll() {
        return ResponseEntity.ok(temaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemaDTO> getById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(temaService.findById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<TemaDTO> create(@RequestBody TemaDTO dto) {
        try {
            if (dto != null) {
                dto.setIdTema(null);
            }
            TemaDTO creado = temaService.save(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemaDTO> update(@PathVariable Integer id,
                                          @RequestBody TemaDTO dto) {
        try {
            temaService.findById(id);
            if (dto != null) {
                dto.setIdTema(id);
            }
            TemaDTO actualizado = temaService.save(dto);
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
            temaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
