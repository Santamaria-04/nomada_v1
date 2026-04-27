package esic.nomada_v1.external;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import esic.nomada_v1.config.ExternalApiProperties;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExternalResourceSearchService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalResourceSearchService.class);

    private final List<ExternalResourceProvider> providers;
    private final ExternalApiProperties properties;

    public ExternalResourceSearchService(List<ExternalResourceProvider> providers,
                                         ExternalApiProperties properties) {
        this.providers = providers;
        this.properties = properties;
    }

    public List<RecursoDTO> search(String termino) {
        return search(termino, Set.of());
    }

    public List<RecursoDTO> search(String termino, Set<Recurso.TipoRecurso> tiposFiltro) {
        List<RecursoDTO> results = new ArrayList<>();
        int limit = Math.max(1, properties.getDefaultLimit());

        for (ExternalResourceProvider provider : providers) {
            if (tiposFiltro != null && !tiposFiltro.isEmpty() && !tiposFiltro.contains(provider.getTipoRecurso())) {
                continue;
            }

            try {
                results.addAll(provider.search(termino, limit));
            } catch (RuntimeException e) {
                LOGGER.warn("Proveedor externo {} fallo para termino '{}': {}",
                        provider.getClass().getSimpleName(), termino, e.getMessage());
            }
        }

        return results;
    }
}
