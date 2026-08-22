package com.example.portalaluno.shared;

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
}
