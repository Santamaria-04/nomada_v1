package esic.nomada_v1.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder URL_DECODER = Base64.getUrlDecoder();
    private static final String HMAC_SHA256 = "HmacSHA256";

    private final String secret;
    private final long expirationMs;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.expiration-ms}") long expirationMs) {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    public String generateToken(AuthenticatedUser user) {
        long now = System.currentTimeMillis();

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"" + escapeJson(user.getUsername()) + "\","
                + "\"userId\":" + user.getIdUsuario() + ","
                + "\"rol\":\"" + escapeJson(user.getRol()) + "\","
                + "\"iat\":" + now + ","
                + "\"exp\":" + (now + expirationMs) + "}";

        String encodedHeader = encodeJson(headerJson);
        String encodedPayload = encodeJson(payloadJson);
        String signature = sign(encodedHeader + "." + encodedPayload);

        return encodedHeader + "." + encodedPayload + "." + signature;
    }

    public JwtClaims parseToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Token JWT inválido");
            }

            String signedContent = parts[0] + "." + parts[1];
            String expectedSignature = sign(signedContent);
            if (!expectedSignature.equals(parts[2])) {
                throw new IllegalArgumentException("Firma JWT inválida");
            }

            Map<String, String> payload = parseJsonObject(new String(URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8));

            Integer userId = Integer.parseInt(payload.get("userId"));
            String email = payload.get("sub");
            String role = payload.get("rol");
            long issuedAt = Long.parseLong(payload.get("iat"));
            long expiration = Long.parseLong(payload.get("exp"));

            if (expiration < System.currentTimeMillis()) {
                throw new IllegalArgumentException("El token ha expirado");
            }

            return new JwtClaims(userId, email, role, issuedAt, expiration);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo procesar el token JWT", e);
        }
    }

    public boolean isTokenValid(String token, AuthenticatedUser user) {
        JwtClaims claims = parseToken(token);
        return user.getIdUsuario().equals(claims.userId())
                && user.getUsername().equals(claims.email())
                && user.getRol().equals(claims.role());
    }

    private String encodeJson(String content) {
        return URL_ENCODER.encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
            return URL_ENCODER.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token JWT", e);
        }
    }

    private Map<String, String> parseJsonObject(String json) {
        try {
            String normalizedJson = json.trim();
            if (!normalizedJson.startsWith("{") || !normalizedJson.endsWith("}")) {
                throw new IllegalArgumentException("Payload JWT inválido");
            }

            String body = normalizedJson.substring(1, normalizedJson.length() - 1);
            Map<String, String> values = new HashMap<>();

            for (String entry : body.split(",")) {
                String[] keyValue = entry.split(":", 2);
                if (keyValue.length != 2) {
                    throw new IllegalArgumentException("Payload JWT inválido");
                }
                String key = unquote(keyValue[0].trim());
                String value = keyValue[1].trim();
                values.put(key, unquote(value));
            }

            return values;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("No se pudo leer el payload del token JWT", e);
        }
    }

    private String unquote(String value) {
        String normalizedValue = value.trim();
        if (normalizedValue.startsWith("\"") && normalizedValue.endsWith("\"")) {
            normalizedValue = normalizedValue.substring(1, normalizedValue.length() - 1);
        }
        return normalizedValue.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
