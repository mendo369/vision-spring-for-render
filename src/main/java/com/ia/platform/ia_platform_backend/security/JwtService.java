// src/main/java/com/ia/platform/ia_platform_backend/security/JwtService.java

package com.ia.platform.ia_platform_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // --- FUNCIÓN PARA EXTRAER EL USER ID ---
    public Long extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        // Asumiendo que el ID del usuario está en el claim "sub" (subject) y es un String que representa un número
        // Si guardaste el ID como un claim personalizado como "userId", cambia "sub" por "userId"
        Object userIdObj = claims.getSubject(); // O claims.get("userId");
        if (userIdObj instanceof String) {
            try {
                return Long.parseLong((String) userIdObj);
            } catch (NumberFormatException e) {
                // Si el subject no es un número, puede que no sea el ID
                // Aquí puedes manejar el error o intentar con otro claim si lo tienes
                // Por ejemplo, si tienes un claim específico como "userId":
                Object customIdObj = claims.get("userId");
                if (customIdObj instanceof Integer) {
                    return ((Integer) customIdObj).longValue();
                } else if (customIdObj instanceof Long) {
                    return (Long) customIdObj;
                } else if (customIdObj instanceof Double) {
                    return ((Double) customIdObj).longValue(); // JWT puede serializar números como Double
                }
                // Si tampoco está en "userId", lanza una excepción o devuelve null
                return null;
            }
        } else if (userIdObj instanceof Integer) {
            return ((Integer) userIdObj).longValue();
        } else if (userIdObj instanceof Long) {
            return (Long) userIdObj;
        } else if (userIdObj instanceof Double) {
            return ((Double) userIdObj).longValue();
        }
        return null;
    }
    // ---

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Opcional: Añadir el ID del usuario al token si no lo haces en el subject
        // claims.put("userId", ((CustomUserDetails) userDetails).getId()); // Asumiendo CustomUserDetails con getId()
        return createToken(claims, userDetails.getUsername());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // <-- El 'subject' aquí es donde normalmente va el username o el ID
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        // Usar la clave directamente como string
        // Esta es la clave que debe tener al menos 256 bits
        // La clave debe ser de al menos 32 bytes (256 bits)
        byte[] keyBytes = secret.getBytes();
        if (keyBytes.length < 32) {
            // Si la clave es demasiado corta, la expandimos
            String longSecret = String.format("%-32s", secret).replace(' ', '0');
            keyBytes = longSecret.getBytes();
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
}