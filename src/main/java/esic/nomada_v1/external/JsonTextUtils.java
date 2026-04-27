package esic.nomada_v1.external;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JsonTextUtils {

    private JsonTextUtils() {
    }

    public static List<String> objectBlocks(String json, String arrayField) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        String arrayBody = arrayBody(json, arrayField);
        List<String> blocks = new ArrayList<>();
        int depth = 0;
        int start = -1;
        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < arrayBody.length(); i++) {
            char current = arrayBody.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (current == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (current == '{') {
                if (depth == 0) {
                    start = i;
                }
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    blocks.add(arrayBody.substring(start, i + 1));
                    start = -1;
                }
            }
        }
        return blocks;
    }

    public static String stringField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return unescape(matcher.group(1));
    }

    public static String nestedStringField(String json, String objectName, String fieldName) {
        String objectBody = objectBody(json, objectName);
        return objectBody == null ? null : stringField(objectBody, fieldName);
    }

    public static String arrayFirstValue(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*\\[\\s*\"((?:\\\\.|[^\"])*)\"");
        Matcher matcher = pattern.matcher(json);
        if (!matcher.find()) {
            return null;
        }
        return unescape(matcher.group(1));
    }

    public static String intField(String json, String fieldName) {
        Pattern pattern = Pattern.compile("\"" + Pattern.quote(fieldName) + "\"\\s*:\\s*(\\d+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    public static String cleanHtml(String value) {
        if (value == null) {
            return null;
        }
        return value.replaceAll("<[^>]+>", "").replace("&quot;", "\"").replace("&amp;", "&");
    }

    private static String arrayBody(String json, String arrayField) {
        if (json == null || json.isBlank()) {
            return "";
        }
        int fieldIndex = json.indexOf("\"" + arrayField + "\"");
        if (fieldIndex < 0) {
            return "";
        }
        int start = json.indexOf('[', fieldIndex);
        if (start < 0) {
            return "";
        }
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char current = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (current == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (current == '[') {
                depth++;
            } else if (current == ']') {
                depth--;
                if (depth == 0) {
                    return json.substring(start + 1, i);
                }
            }
        }
        return "";
    }

    public static String objectBody(String json, String objectName) {
        if (json == null || json.isBlank()) {
            return null;
        }
        int fieldIndex = json.indexOf("\"" + objectName + "\"");
        if (fieldIndex < 0) {
            return null;
        }
        int start = json.indexOf('{', fieldIndex);
        if (start < 0) {
            return null;
        }
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int i = start; i < json.length(); i++) {
            char current = json.charAt(i);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (current == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (current == '{') {
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0) {
                    return json.substring(start, i + 1);
                }
            }
        }
        return null;
    }

    private static String unescape(String value) {
        return value.replace("\\\"", "\"")
                .replace("\\/", "/")
                .replace("\\n", " ")
                .replace("\\t", " ")
                .replace("\\r", " ")
                .replace("\\\\", "\\");
    }
}
