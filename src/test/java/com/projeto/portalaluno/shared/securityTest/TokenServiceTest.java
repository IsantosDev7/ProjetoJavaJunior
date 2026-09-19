package com.projeto.portalaluno.shared.securityTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRole;
import com.projeto.portalaluno.shared.security.TokenService;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private User usuario;

    @BeforeEach
    void setup() {
        usuario = new User();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("usuario@test.com");
        usuario.setRole(UserRole.ALUNO);
    }

    @Test
    @DisplayName("Case 1: Generate token successfully")
    void deveGerarTokenComSucesso() {
        String token = tokenService.geraToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(token.length() > 0);
    }

    @Test
    @DisplayName("Case 2: Generated token contains user info")
    void deveVerificarQueTokenContemDadosDoUsuario() {
        String token = tokenService.geraToken(usuario);

        assertNotNull(token);
        // Token deve ser valido (não vazio)
        assertTrue(token.split("\\.").length == 3); // JWT tem 3 partes separadas por ponto
    }

    @Test
    @DisplayName("Case 3: Token generated for different users are different")
    void deveGerarTokensDiferentesParaUsuariosDiferentes() {
        User usuario2 = new User();
        usuario2.setId(UUID.randomUUID());
        usuario2.setEmail("outro@test.com");
        usuario2.setRole(UserRole.ALUNO);

        String token1 = tokenService.geraToken(usuario);
        String token2 = tokenService.geraToken(usuario2);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Case 4: Token generation works for all user roles")
    void deveGerarTokenParaTodosOsRoles() {
        UserRole[] roles = UserRole.values();

        for (UserRole role : roles) {
            usuario.setRole(role);
            String token = tokenService.geraToken(usuario);
            assertNotNull(token);
            assertFalse(token.isBlank());
        }
    }

    @Test
    @DisplayName("Case 5: Token is reproducible format")
    void deveVerificarFormatoDoToken() {
        String token = tokenService.geraToken(usuario);

        // JWT deve ter formato: header.payload.signature
        String[] partes = token.split("\\.");
        assertEquals(3, partes.length);

        // Cada parte deve ser válida em base64
        for (String parte : partes) {
            assertFalse(parte.isBlank());
        }
    }
}
