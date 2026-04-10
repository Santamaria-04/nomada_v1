package esic.nomada_v1.service;

import org.springframework.stereotype.Service;
import esic.nomada_v1.dto.BusquedaResponseDTO;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.dto.AportacionDTO;
import esic.nomada_v1.repository.AportacionRepository;
import esic.nomada_v1.repository.RecursoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusquedaService {

    private final RecursoRepository recursoRepository;
    private final AportacionRepository aportacionRepository;
    private final FabricaRecursoService fabricaRecurso;
    private final FabricaAportacionService fabricaAportacion;
    private final HistorialService historialService;

    public BusquedaService(RecursoRepository recursoRepository,
                           AportacionRepository aportacionRepository,
                           FabricaRecursoService fabricaRecurso,
                           FabricaAportacionService fabricaAportacion,
                           HistorialService historialService) {
        this.recursoRepository = recursoRepository;
        this.aportacionRepository = aportacionRepository;
        this.fabricaRecurso = fabricaRecurso;
        this.fabricaAportacion = fabricaAportacion;
        this.historialService = historialService;
    }

    @Transactional
    public BusquedaResponseDTO search(Integer idUsuario, String termino) {
        if (termino == null || termino.trim().length() < 2) {
            throw new IllegalArgumentException("El término de búsqueda debe tener al menos 2 caracteres");
        }

        String normalizedTerm = termino.trim();
        historialService.registrarBusqueda(idUsuario, normalizedTerm);

        List<RecursoDTO> recursos = recursoRepository
                .findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(normalizedTerm, normalizedTerm)
                .stream()
                .map(fabricaRecurso::createRecursoDTO)
                .collect(Collectors.toList());

        List<AportacionDTO> aportaciones = aportacionRepository
                .findByContenidoContainingIgnoreCaseAndEliminadaFalse(normalizedTerm)
                .stream()
                .map(fabricaAportacion::createAportacionDTO)
                .collect(Collectors.toList());

        return new BusquedaResponseDTO(normalizedTerm, recursos, aportaciones);
    }
}
