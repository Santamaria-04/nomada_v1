package esic.nomada_v1.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.TemaDTO;
import esic.nomada_v1.model.Tema;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.TemaRepository;
import esic.nomada_v1.repository.RecursoRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class TemaService {

    private final TemaRepository temaRepository;
    private final FabricaTemaService fabricaTema;
    private final RecursoRepository recursoRepository;
    private final AportacionRepository aportacionRepository;

    public TemaService(TemaRepository temaRepository,
                       FabricaTemaService fabricaTema,
                       RecursoRepository recursoRepository,
                       AportacionRepository aportacionRepository) {
        this.temaRepository = temaRepository;
        this.fabricaTema = fabricaTema;
        this.recursoRepository = recursoRepository;
        this.aportacionRepository = aportacionRepository;
    }

    @Transactional
    public TemaDTO save(TemaDTO dto) {
        validateDto(dto);

        Tema entidad = fabricaTema.createTema(dto);
        entidad.setNombre(dto.getNombre().trim());
        entidad.setDescripcion(normalizeOptionalText(dto.getDescripcion()));

        Tema guardado = temaRepository.save(entidad);
        return fabricaTema.createTemaDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<TemaDTO> findAll() {
        return temaRepository.findAll()
                .stream()
                .map(fabricaTema::createTemaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TemaDTO findById(Integer id) {
        Tema tema = temaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tema no encontrado"));

        return fabricaTema.createTemaDTO(tema);
    }

    @Transactional
    public void deleteById(Integer id) {
        if (!temaRepository.existsById(id)) {
            throw new NoSuchElementException("Tema no encontrado");
        }
        if (recursoRepository.existsByTema_IdTema(id)) {
            throw new IllegalArgumentException("No se puede eliminar un tema con recursos asociados");
        }
        if (aportacionRepository.existsByTema_IdTema(id)) {
            throw new IllegalArgumentException("No se puede eliminar un tema con aportaciones asociadas");
        }
        temaRepository.deleteById(id);
    }

    private void validateDto(TemaDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del tema es obligatorio");
        }
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }
}
