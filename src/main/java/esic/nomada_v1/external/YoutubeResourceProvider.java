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
public class YoutubeResourceProvider implements ExternalResourceProvider {

    private final ExternalApiClient externalApiClient;
    private final ExternalApiProperties properties;

    public YoutubeResourceProvider(ExternalApiClient externalApiClient,
                                   ExternalApiProperties properties) {
        this.externalApiClient = externalApiClient;
        this.properties = properties;
    }

    @Override
    public Recurso.TipoRecurso getTipoRecurso() {
        return Recurso.TipoRecurso.VIDEO;
    }

    @Override
    public List<RecursoDTO> search(String termino, int limit) {
        if (!properties.isYoutubeEnabled() || properties.getYoutubeApiKey() == null || properties.getYoutubeApiKey().isBlank()) {
            return List.of();
        }

        String encodedTerm = URLEncoder.encode(termino, StandardCharsets.UTF_8);
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video"
                + "&maxResults=" + limit
                + "&q=" + encodedTerm
                + "&key=" + URLEncoder.encode(properties.getYoutubeApiKey(), StandardCharsets.UTF_8);

        return parseResults(externalApiClient.get(url));
    }

    List<RecursoDTO> parseResults(String json) {
        List<RecursoDTO> results = new ArrayList<>();

        for (String item : JsonTextUtils.objectBlocks(json, "items")) {
            String videoId = JsonTextUtils.nestedStringField(item, "id", "videoId");
            String title = JsonTextUtils.nestedStringField(item, "snippet", "title");
            String description = JsonTextUtils.nestedStringField(item, "snippet", "description");
            String publishedAt = JsonTextUtils.nestedStringField(item, "snippet", "publishedAt");
            String thumbnailUrl = extractThumbnailUrl(item);

            if (videoId == null || videoId.isBlank() || title == null || title.isBlank()) {
                continue;
            }

            RecursoDTO dto = new RecursoDTO();
            dto.setTitulo(title);
            dto.setDescripcion(description);
            dto.setImagenUrl(thumbnailUrl);
            dto.setTipoRecurso(Recurso.TipoRecurso.VIDEO);
            dto.setFuente("YouTube");
            dto.setUrlEnlace("https://www.youtube.com/watch?v=" + videoId);

            if (publishedAt != null && !publishedAt.isBlank()) {
                dto.setFechaPublicacion(OffsetDateTime.parse(publishedAt).toLocalDate());
            }

            results.add(dto);
        }

        return results;
    }

    private String extractThumbnailUrl(String itemJson) {
        String snippet = JsonTextUtils.objectBody(itemJson, "snippet");
        if (snippet == null || snippet.isBlank()) {
            return null;
        }
        String thumbnails = JsonTextUtils.objectBody(snippet, "thumbnails");
        if (thumbnails == null || thumbnails.isBlank()) {
            return null;
        }

        String url = thumbnailUrlForSize(thumbnails, "high");
        if (url != null) {
            return url;
        }
        url = thumbnailUrlForSize(thumbnails, "medium");
        if (url != null) {
            return url;
        }
        return thumbnailUrlForSize(thumbnails, "default");
    }

    private String thumbnailUrlForSize(String thumbnailsJson, String sizeKey) {
        String sizeObject = JsonTextUtils.objectBody(thumbnailsJson, sizeKey);
        if (sizeObject == null || sizeObject.isBlank()) {
            return null;
        }
        String url = JsonTextUtils.stringField(sizeObject, "url");
        return (url == null || url.isBlank()) ? null : url;
    }
}
