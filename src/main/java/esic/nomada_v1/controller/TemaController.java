package esic.nomada_v1.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import esic.nomada_v1.dto.TemaDTO;
import esic.nomada_v1.service.TemaService;

import java.util.List;

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
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<TemaDTO> create(@RequestBody TemaDTO dto) {
        dto.setIdTema(null);
        TemaDTO creado = temaService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemaDTO> update(@PathVariable Integer id,
                                          @RequestBody TemaDTO dto) {
        try {
            temaService.findById(id);
            dto.setIdTema(id);
            TemaDTO actualizado = temaService.save(dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            temaService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
