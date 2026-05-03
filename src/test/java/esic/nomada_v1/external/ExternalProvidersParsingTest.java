package esic.nomada_v1.external;

import esic.nomada_v1.config.ExternalApiProperties;
import esic.nomada_v1.dto.RecursoDTO;
import esic.nomada_v1.model.Recurso;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalProvidersParsingTest {

    private final ExternalApiProperties properties = new ExternalApiProperties(5, true, true, true, true, "api-key");
    private final ExternalApiClient externalApiClient = new ExternalApiClient(HttpClient.newHttpClient());

    @Test
    void shouldParseWikipediaResults() {
        WikipediaResourceProvider provider = new WikipediaResourceProvider(externalApiClient, properties);
        String json = """
                {
                  "query": {
                    "search": [
                      {
                        "title": "Java",
                        "snippet": "Lenguaje de <b>programacion</b>"
                      }
                    ]
                  }
                }
                """;

        List<RecursoDTO> results = provider.parseResults(json);

        assertEquals(1, results.size());
        assertEquals("Java", results.get(0).getTitulo());
        assertEquals("Lenguaje de programacion", results.get(0).getDescripcion());
        assertEquals(Recurso.TipoRecurso.ARTICULO, results.get(0).getTipoRecurso());
    }

    @Test
    void shouldParseOpenLibraryResults() {
        OpenLibraryResourceProvider provider = new OpenLibraryResourceProvider(externalApiClient, properties);
        String json = """
                {
                  "docs": [
                    {
                      "title": "Effective Java",
                      "key": "/works/OL45804W",
                      "cover_i": 12345,
                      "author_name": ["Joshua Bloch"],
                      "first_publish_year": 2001
                    }
                  ]
                }
                """;

        List<RecursoDTO> results = provider.parseResults(json, "effective+java");

        assertEquals(1, results.size());
        assertEquals("Effective Java", results.get(0).getTitulo());
        assertEquals("Autor: Joshua Bloch", results.get(0).getDescripcion());
        assertEquals("https://openlibrary.org/works/OL45804W", results.get(0).getUrlEnlace());
        assertEquals("https://covers.openlibrary.org/b/id/12345-M.jpg", results.get(0).getImagenUrl());
        assertEquals(Recurso.TipoRecurso.LIBRO, results.get(0).getTipoRecurso());
    }

    @Test
    void shouldParseItunesResults() {
        ItunesPodcastResourceProvider provider = new ItunesPodcastResourceProvider(externalApiClient, properties);
        String json = """
                {
                  "results": [
                    {
                      "collectionName": "Podcast Java",
                      "artistName": "Nomada",
                      "collectionViewUrl": "https://podcasts.apple.com/podcast-java",
                      "artworkUrl100": "https://example.test/podcast.jpg",
                      "releaseDate": "2024-01-10T10:00:00Z"
                    }
                  ]
                }
                """;

        List<RecursoDTO> results = provider.parseResults(json);

        assertEquals(1, results.size());
        assertEquals("Podcast Java", results.get(0).getTitulo());
        assertEquals("Podcast de Nomada", results.get(0).getDescripcion());
        assertEquals("https://example.test/podcast.jpg", results.get(0).getImagenUrl());
        assertEquals(Recurso.TipoRecurso.PODCAST, results.get(0).getTipoRecurso());
    }

    @Test
    void shouldParseYoutubeResults() {
        YoutubeResourceProvider provider = new YoutubeResourceProvider(externalApiClient, properties);
        String json = """
                {
                  "items": [
                    {
                      "id": {
                        "videoId": "abc123"
                      },
                      "snippet": {
                        "title": "Curso de Java",
                        "description": "Introduccion",
                        "thumbnails": {
                          "high": {
                            "url": "https://i.ytimg.com/vi/abc123/hqdefault.jpg"
                          }
                        },
                        "publishedAt": "2024-03-15T12:30:00Z"
                      }
                    }
                  ]
                }
                """;

        List<RecursoDTO> results = provider.parseResults(json);

        assertEquals(1, results.size());
        assertEquals("Curso de Java", results.get(0).getTitulo());
        assertEquals("https://www.youtube.com/watch?v=abc123", results.get(0).getUrlEnlace());
        assertEquals("https://i.ytimg.com/vi/abc123/hqdefault.jpg", results.get(0).getImagenUrl());
        assertEquals(Recurso.TipoRecurso.VIDEO, results.get(0).getTipoRecurso());
    }
}
