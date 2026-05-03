package esic.nomada_v1.external;

import org.springframework.stereotype.Component;
import esic.nomada_v1.config.ExternalApiProperties;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItunesPodcastResourceProvider implements ExternalResourceProvider {

    private final ExternalApiClient externalApiClient;
    private final ExternalApiProperties properties;

    public ItunesPodcastResourceProvider(ExternalApiClient externalApiClient,
                                         ExternalApiProperties properties) {
        this.externalApiClient = externalApiClient;
        this.properties = properties;
    }

    @Override
    public Recurso.TipoRecurso getTipoRecurso() {
        return Recurso.TipoRecurso.PODCAST;
    }

    @Override
    public List<RecursoDTO> search(String termino, int limit) {
        if (!properties.isItunesEnabled()) {
            return List.of();
        }

        String encodedTerm = URLEncoder.encode(termino, StandardCharsets.UTF_8);
        String url = "https://itunes.apple.com/search?media=podcast&country=ES&limit=" + limit + "&term=" + encodedTerm;

        return parseResults(externalApiClient.get(url));
    }

    List<RecursoDTO> parseResults(String json) {
        List<RecursoDTO> results = new ArrayList<>();

        for (String item : JsonTextUtils.objectBlocks(json, "results")) {
            String title = JsonTextUtils.stringField(item, "collectionName");
            String artist = JsonTextUtils.stringField(item, "artistName");
            String link = JsonTextUtils.stringField(item, "collectionViewUrl");
            String releaseDate = JsonTextUtils.stringField(item, "releaseDate");
            String artworkUrl = firstNonBlank(
                    JsonTextUtils.stringField(item, "artworkUrl600"),
                    JsonTextUtils.stringField(item, "artworkUrl100"),
                    JsonTextUtils.stringField(item, "artworkUrl60")
            );

            if (title == null || title.isBlank() || link == null || link.isBlank()) {
                continue;
            }

            RecursoDTO dto = new RecursoDTO();
            dto.setTitulo(title);
            dto.setDescripcion(artist == null ? null : "Podcast de " + artist);
            dto.setImagenUrl(artworkUrl);
            dto.setTipoRecurso(Recurso.TipoRecurso.PODCAST);
            dto.setFuente("Apple Podcasts");
            dto.setUrlEnlace(link);

            if (releaseDate != null && !releaseDate.isBlank()) {
                dto.setFechaPublicacion(OffsetDateTime.parse(releaseDate).toLocalDate());
            }

            results.add(dto);
        }

        return results;
    }

    private String firstNonBlank(String... candidates) {
        if (candidates == null) {
            return null;
        }
        for (String candidate : candidates) {
            if (candidate != null && !candidate.isBlank()) {
                return candidate;
            }
        }
        return null;
    }
}
