package esic.nomada_v1.util;

import java.util.regex.Pattern;

public final class TextValidationUtils {

    private static final Pattern EXTERNAL_LINK_PATTERN =
            Pattern.compile("(https?://\\S+|www\\.\\S+)", Pattern.CASE_INSENSITIVE);

    private TextValidationUtils() {
    }

    public static boolean containsExternalLink(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return EXTERNAL_LINK_PATTERN.matcher(text).find();
    }
}
