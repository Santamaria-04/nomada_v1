package esic.nomada_v1.external;

import org.springframework.stereotype.Component;
import esic.nomada_v1.config.ExternalApiProperties;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class WikipediaResourceProvider implements ExternalResourceProvider {

    private final ExternalApiClient externalApiClient;
    private final ExternalApiProperties properties;

    public WikipediaResourceProvider(ExternalApiClient externalApiClient,
                                     ExternalApiProperties properties) {
        this.externalApiClient = externalApiClient;
        this.properties = properties;
    }

    @Override
    public Recurso.TipoRecurso getTipoRecurso() {
        return Recurso.TipoRecurso.ARTICULO;
    }

    @Override
    public List<RecursoDTO> search(String termino, int limit) {
        if (!properties.isWikipediaEnabled()) {
            return List.of();
        }

        String encodedTerm = URLEncoder.encode(termino, StandardCharsets.UTF_8);
        String url = "https://es.wikipedia.org/w/api.php?action=query&list=search&format=json"
                + "&srlimit=" + limit
                + "&srsearch=" + encodedTerm;

        return parseResults(externalApiClient.get(url));
    }

    List<RecursoDTO> parseResults(String json) {
        List<RecursoDTO> results = new ArrayList<>();

        for (String item : JsonTextUtils.objectBlocks(JsonTextUtils.objectBody(json, "query"), "search")) {
            String title = JsonTextUtils.stringField(item, "title");
            String snippet = JsonTextUtils.cleanHtml(JsonTextUtils.stringField(item, "snippet"));

            if (title == null || title.isBlank()) {
                continue;
            }

            RecursoDTO dto = new RecursoDTO();
            dto.setTitulo(title);
            dto.setDescripcion(snippet);
            dto.setTipoRecurso(Recurso.TipoRecurso.ARTICULO);
            dto.setFuente("Wikipedia");
            dto.setUrlEnlace("https://es.wikipedia.org/wiki/" + URLEncoder.encode(title.replace(" ", "_"), StandardCharsets.UTF_8));
            results.add(dto);
        }

        return results;
    }
}
