package esic.nomada_v1.external;

import org.junit.jupiter.api.Test;
import esic.nomada_v1.config.ExternalApiProperties;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalResourceSearchServiceTest {

    @Test
    void shouldOnlyCallProvidersMatchingSelectedTypes() {
        FakeProvider videoProvider = new FakeProvider(Recurso.TipoRecurso.VIDEO);
        FakeProvider bookProvider = new FakeProvider(Recurso.TipoRecurso.LIBRO);
        ExternalApiProperties properties = new ExternalApiProperties(5, true, true, true, true, "");
        ExternalResourceSearchService service = new ExternalResourceSearchService(
                List.of(videoProvider, bookProvider),
                properties
        );

        List<RecursoDTO> results = service.search("java", Set.of(Recurso.TipoRecurso.VIDEO));

        assertEquals(1, results.size());
        assertEquals(Recurso.TipoRecurso.VIDEO, results.get(0).getTipoRecurso());
        assertEquals(1, videoProvider.calls);
        assertEquals(0, bookProvider.calls);
    }

    private static class FakeProvider implements ExternalResourceProvider {

        private final Recurso.TipoRecurso tipoRecurso;
        private int calls;

        private FakeProvider(Recurso.TipoRecurso tipoRecurso) {
            this.tipoRecurso = tipoRecurso;
        }

        @Override
        public Recurso.TipoRecurso getTipoRecurso() {
            return tipoRecurso;
        }

        @Override
        public List<RecursoDTO> search(String termino, int limit) {
            calls++;

            RecursoDTO dto = new RecursoDTO();
            dto.setTitulo(termino);
            dto.setTipoRecurso(tipoRecurso);
            return List.of(dto);
        }
    }
}
