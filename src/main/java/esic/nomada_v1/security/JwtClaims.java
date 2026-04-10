package esic.nomada_v1.security;

public record JwtClaims(Integer userId, String email, String role, long issuedAt, long expiration) {
}
