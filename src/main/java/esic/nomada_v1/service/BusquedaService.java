package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import esic.nomada_v1.dto.BusquedaResponseDTO;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.dto.AportacionDTO;
import esic.nomada_v1.external.ExternalResourceSearchService;
import esic.nomada_v1.model.Recurso;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.RecursoRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BusquedaService {

    private final RecursoRepository recursoRepository;
    private final AportacionRepository aportacionRepository;
    private final FabricaRecursoService fabricaRecurso;
    private final FabricaAportacionService fabricaAportacion;
    private final HistorialService historialService;
    private final ExternalResourceSearchService externalResourceSearchService;

    public BusquedaService(RecursoRepository recursoRepository,
                           AportacionRepository aportacionRepository,
                           FabricaRecursoService fabricaRecurso,
                           FabricaAportacionService fabricaAportacion,
                           HistorialService historialService,
                           ExternalResourceSearchService externalResourceSearchService) {
        this.recursoRepository = recursoRepository;
        this.aportacionRepository = aportacionRepository;
        this.fabricaRecurso = fabricaRecurso;
        this.fabricaAportacion = fabricaAportacion;
        this.historialService = historialService;
        this.externalResourceSearchService = externalResourceSearchService;
    }

    @Transactional
    public BusquedaResponseDTO search(Integer idUsuario, String termino, String tipos) {
        if (termino == null || termino.trim().length() < 2) {
            throw new IllegalArgumentException("El término de búsqueda debe tener al menos 2 caracteres");
        }

        String normalizedTerm = termino.trim();
        Set<Recurso.TipoRecurso> tiposFiltro = parseTipos(tipos);
        historialService.registrarBusqueda(idUsuario, normalizedTerm);

        List<RecursoDTO> recursosLocales = recursoRepository
                .findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(normalizedTerm, normalizedTerm)
                .stream()
                .map(fabricaRecurso::createRecursoDTO)
                .filter(recurso -> matchesTipo(recurso, tiposFiltro))
                .collect(Collectors.toList());

        List<RecursoDTO> recursosExternos = externalResourceSearchService.search(normalizedTerm, tiposFiltro);

        List<AportacionDTO> aportaciones = aportacionRepository
                .findByContenidoContainingIgnoreCaseAndEliminadaFalse(normalizedTerm)
                .stream()
                .map(fabricaAportacion::createAportacionDTO)
                .collect(Collectors.toList());

        return new BusquedaResponseDTO(normalizedTerm, recursosLocales, recursosExternos, aportaciones);
    }

    private Set<Recurso.TipoRecurso> parseTipos(String tipos) {
        if (tipos == null || tipos.isBlank()) {
            return Set.of();
        }

        return Arrays.stream(tipos.split(","))
                .map(String::trim)
                .filter(tipo -> !tipo.isBlank())
                .map(tipo -> {
                    try {
                        return Recurso.TipoRecurso.valueOf(tipo.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Tipo de recurso no válido: " + tipo);
                    }
                })
                .collect(Collectors.toSet());
    }

    private boolean matchesTipo(RecursoDTO recurso, Set<Recurso.TipoRecurso> tiposFiltro) {
        return tiposFiltro.isEmpty() || tiposFiltro.contains(recurso.getTipoRecurso());
    }
}
