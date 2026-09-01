package com.example.portalaluno.shared.security;

import com.example.portalaluno.auth.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    public String geraToken(User user){

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        Date now = new Date();
        Date expiryDate = new Date(System.currentTimeMillis() + 86400000);

        String token = Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole().toString())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();

        return token;

    }

    public String validaToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject(); // Retorna o e-mail
        } catch (Exception e) {
            System.out.println("❌ Erro ao validar o token: " + e.getMessage());
            return ""; // Se o token for inválido/expirado, retorna vazio
        }
    }
}
