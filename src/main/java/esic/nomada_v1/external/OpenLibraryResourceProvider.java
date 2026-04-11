package esic.nomada_v1.external;

import org.springframework.stereotype.Component;
import esic.nomada_v1.config.ExternalApiProperties;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenLibraryResourceProvider implements ExternalResourceProvider {

    private final ExternalApiClient externalApiClient;
    private final ExternalApiProperties properties;

    public OpenLibraryResourceProvider(ExternalApiClient externalApiClient,
                                       ExternalApiProperties properties) {
        this.externalApiClient = externalApiClient;
        this.properties = properties;
    }

    @Override
    public Recurso.TipoRecurso getTipoRecurso() {
        return Recurso.TipoRecurso.LIBRO;
    }

    @Override
    public List<RecursoDTO> search(String termino, int limit) {
        if (!properties.isOpenLibraryEnabled()) {
            return List.of();
        }

        String encodedTerm = URLEncoder.encode(termino, StandardCharsets.UTF_8);
        String url = "https://openlibrary.org/search.json?limit=" + limit + "&q=" + encodedTerm;

        String json = externalApiClient.get(url);
        List<RecursoDTO> results = new ArrayList<>();

        for (String item : JsonTextUtils.objectBlocks(json, "docs")) {
            String title = JsonTextUtils.stringField(item, "title");
            String key = JsonTextUtils.stringField(item, "key");
            String author = JsonTextUtils.arrayFirstValue(item, "author_name");
            String firstPublishYear = JsonTextUtils.intField(item, "first_publish_year");

            if (title == null || title.isBlank()) {
                continue;
            }

            RecursoDTO dto = new RecursoDTO();
            dto.setTitulo(title);
            dto.setDescripcion(author == null ? null : "Autor: " + author);
            dto.setTipoRecurso(Recurso.TipoRecurso.LIBRO);
            dto.setFuente("Open Library");
            dto.setUrlEnlace(key == null ? "https://openlibrary.org/search?q=" + encodedTerm : "https://openlibrary.org" + key);

            if (firstPublishYear != null) {
                dto.setFechaPublicacion(LocalDate.of(Integer.parseInt(firstPublishYear), 1, 1));
            }

            results.add(dto);
        }

        return results;
    }
}
