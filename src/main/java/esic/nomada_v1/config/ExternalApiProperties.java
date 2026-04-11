package esic.nomada_v1.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExternalApiProperties {

    private final int defaultLimit;
    private final boolean wikipediaEnabled;
    private final boolean openLibraryEnabled;
    private final boolean itunesEnabled;
    private final boolean youtubeEnabled;
    private final String youtubeApiKey;

    public ExternalApiProperties(@Value("${external.apis.default-limit:5}") int defaultLimit,
                                 @Value("${external.apis.wikipedia.enabled:true}") boolean wikipediaEnabled,
                                 @Value("${external.apis.open-library.enabled:true}") boolean openLibraryEnabled,
                                 @Value("${external.apis.itunes.enabled:true}") boolean itunesEnabled,
                                 @Value("${external.apis.youtube.enabled:true}") boolean youtubeEnabled,
                                 @Value("${external.apis.youtube.api-key:}") String youtubeApiKey) {
        this.defaultLimit = defaultLimit;
        this.wikipediaEnabled = wikipediaEnabled;
        this.openLibraryEnabled = openLibraryEnabled;
        this.itunesEnabled = itunesEnabled;
        this.youtubeEnabled = youtubeEnabled;
        this.youtubeApiKey = youtubeApiKey;
    }

    public int getDefaultLimit() {
        return defaultLimit;
    }

    public boolean isWikipediaEnabled() {
        return wikipediaEnabled;
    }

    public boolean isOpenLibraryEnabled() {
        return openLibraryEnabled;
    }

    public boolean isItunesEnabled() {
        return itunesEnabled;
    }

    public boolean isYoutubeEnabled() {
        return youtubeEnabled;
    }

    public String getYoutubeApiKey() {
        return youtubeApiKey;
    }
}
