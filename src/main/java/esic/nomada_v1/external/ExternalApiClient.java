package esic.nomada_v1.external;

import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class ExternalApiClient {

    private final HttpClient httpClient;

    public ExternalApiClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public String get(String url) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(8))
                    .header("Accept", "application/json")
                    .header("User-Agent", "NomadaTFG/1.0")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("External API returned status " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo consultar la API externa", e);
        }
    }
}
