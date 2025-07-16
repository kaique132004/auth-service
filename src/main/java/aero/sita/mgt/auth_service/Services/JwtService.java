package aero.sita.mgt.auth_service.Services;

import aero.sita.mgt.auth_service.Schemas.Entitys.UserEntity;
import aero.sita.mgt.auth_service.Schemas.Entitys.UserPermissions;
import aero.sita.mgt.auth_service.Schemas.Entitys.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    public static final long EXPIRATION_TIME = 1000 * 60 * 60 * 2; // 2 horas
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.aes-secret}")
    private String aesSecret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }


    public String generateToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();

        // Authorities (roles) — se tiver campo de role ou authorities direto
        claims.put("authorities", List.of(user.getRole()));

        // Custom permissions
        List<String> permissions = user.getPermissions().stream()
                .map(UserPermissions::getPermissionName)
                .collect(Collectors.toList());
        claims.put("permissions", permissions);

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        return encryptAES(jwt);
    }



    public String generatePasswordResetToken(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("reset", true);

        String jwt = Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date()) // corrigido
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 15)) // 15 minutos
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();

        return encryptAES(jwt);
    }



    public String extractUsername(String token) {
        String decrypted = decryptAES(token);
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(decrypted)
                .getBody()
                .getSubject();
    }

    public Claims extractAllClaims(String token) {
        String decrypted = decryptAES(token); // Faltava isso
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(decrypted)
                .getBody();
    }

    public boolean isResetToken(String token) {
        Claims claims = extractAllClaims(token);
        return Boolean.TRUE.equals(claims.get("reset", Boolean.class));
    }

    public boolean isTokenValid(String token) {
        try {
            String decrypted = decryptAES(token);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(decrypted)
                    .getBody();

            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // AES Encrypt/Decrypt
    private String encryptAES(String value) {
        try {
            SecretKeySpec key = new SecretKeySpec(aesSecret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao criptografar token", ex);
        }
    }

    private String decryptAES(String encryptedValue) {
        try {
            SecretKeySpec key = new SecretKeySpec(aesSecret.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao descriptografar token", ex);
        }
    }
}
