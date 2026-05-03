package esic.nomada_v1.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;
import esic.nomada_v1.model.Tema;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.FavoritoRepository;
import esic.nomada_v1.repository.HistorialRepository;
import esic.nomada_v1.repository.RecursoRepository;
import esic.nomada_v1.repository.TemaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class RecursoService {

    private final RecursoRepository recursoRepository;
    private final TemaRepository temaRepository;
    private final FabricaRecursoService fabricaRecurso;
    private final HistorialService historialService;
    private final AportacionRepository aportacionRepository;
    private final FavoritoRepository favoritoRepository;
    private final HistorialRepository historialRepository;

    public RecursoService(RecursoRepository recursoRepository,
                          TemaRepository temaRepository,
                          FabricaRecursoService fabricaRecurso,
                          HistorialService historialService,
                          AportacionRepository aportacionRepository,
                          FavoritoRepository favoritoRepository,
                          HistorialRepository historialRepository) {
        this.recursoRepository = recursoRepository;
        this.temaRepository = temaRepository;
        this.fabricaRecurso = fabricaRecurso;
        this.historialService = historialService;
        this.aportacionRepository = aportacionRepository;
        this.favoritoRepository = favoritoRepository;
        this.historialRepository = historialRepository;
    }

    @Transactional
    public RecursoDTO save(RecursoDTO dto) {
        validateDto(dto);

        Recurso entidad = fabricaRecurso.createRecurso(dto);
        entidad.setTitulo(dto.getTitulo().trim());
        entidad.setUrlEnlace(dto.getUrlEnlace().trim());
        entidad.setDescripcion(normalizeOptionalText(dto.getDescripcion()));
        entidad.setImagenUrl(normalizeOptionalText(dto.getImagenUrl()));
        entidad.setFuente(dto.getFuente().trim());

        if (dto.getIdTema() != null) {
            Tema tema = temaRepository.findById(dto.getIdTema())
                    .orElseThrow(() -> new NoSuchElementException("Tema no encontrado"));
            entidad.setTema(tema);
        }

        Recurso guardado = recursoRepository.save(entidad);
        return fabricaRecurso.createRecursoDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<RecursoDTO> findAll() {
        return recursoRepository.findAll()
                .stream()
                .map(fabricaRecurso::createRecursoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RecursoDTO> findByTema(Integer idTema) {
        return recursoRepository.findByTema_IdTema(idTema)
                .stream()
                .map(fabricaRecurso::createRecursoDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RecursoDTO findById(Integer id, Integer idUsuario) {
        Recurso recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Recurso no encontrado"));
        if (idUsuario != null) {
            historialService.registrarConsultaRecurso(idUsuario, id);
        }

        return fabricaRecurso.createRecursoDTO(recurso);
    }

    @Transactional
    public void deleteById(Integer id) {
        if (!recursoRepository.existsById(id)) {
            throw new NoSuchElementException("Recurso no encontrado");
        }
        if (aportacionRepository.existsByRecurso_IdRecurso(id)) {
            throw new IllegalArgumentException("No se puede eliminar un recurso con aportaciones asociadas");
        }
        if (favoritoRepository.existsByRecurso_IdRecurso(id)) {
            throw new IllegalArgumentException("No se puede eliminar un recurso que está en favoritos");
        }
        if (historialRepository.existsByRecurso_IdRecurso(id)) {
            throw new IllegalArgumentException("No se puede eliminar un recurso con historial asociado");
        }
        recursoRepository.deleteById(id);
    }

    private void validateDto(RecursoDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El cuerpo de la petición es obligatorio");
        }
        if (isBlank(dto.getTitulo())) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (isBlank(dto.getUrlEnlace())) {
            throw new IllegalArgumentException("La URL del recurso es obligatoria");
        }
        if (dto.getTipoRecurso() == null) {
            throw new IllegalArgumentException("El tipo de recurso es obligatorio");
        }
        if (isBlank(dto.getFuente())) {
            throw new IllegalArgumentException("La fuente del recurso es obligatoria");
        }
        if (dto.getFechaPublicacion() != null && dto.getFechaPublicacion().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de publicación no puede ser futura");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();
        return normalizedValue.isEmpty() ? null : normalizedValue;
    }
}
