package com.projeto.portalaluno.authTest.tokenconviteTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRepository;
import com.projeto.portalaluno.auth.UserRole;
import com.projeto.portalaluno.auth.tokenconvite.TokenConvite;
import com.projeto.portalaluno.auth.tokenconvite.TokenConviteRepository;
import com.projeto.portalaluno.auth.tokenconvite.TokenConviteService;
import com.projeto.portalaluno.shared.notificacao.NotificacaoService;
import com.projeto.portalaluno.shared.notificacao.TipoNotificacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenConviteServiceTest {

    @Mock
    private TokenConviteRepository tokenConviteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private TokenConviteService tokenConviteService;

    private User user;
    private TokenConvite tokenConvite;
    private String token;
    private String password;

    @BeforeEach
    void setup() {


        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("novousuario@example.com");
        user.setPassword(null);
        user.setRole(UserRole.ALUNO);
        user.setEnabled(false);

        token = UUID.randomUUID().toString();
        password = "NovaSenh@123";

        tokenConvite = new TokenConvite();
        tokenConvite.setId(UUID.randomUUID());
        tokenConvite.setToken(token);
        tokenConvite.setUser(user);
        tokenConvite.setCreatedAt(OffsetDateTime.now());
    }

    @Test
    @DisplayName("Generate and send invite: creates new token and sends notification")
    void gerarEEnviarConvite_noExistingToken_createsAndSends() {
        when(tokenConviteRepository.findByUser(eq(user))).thenReturn(Optional.empty());
        when(tokenConviteRepository.save(any(TokenConvite.class))).thenReturn(tokenConvite);

        tokenConviteService.gerarEEnviarConvite(user);

        verify(tokenConviteRepository).save(any(TokenConvite.class));
        verify(notificacaoService).notificarEmail(
                eq(TipoNotificacao.DEFINIR_SENHA),
                eq(user),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Generate and send invite: deletes old token before creating new one")
    void gerarEEnviarConvite_existingToken_deletesOldAndCreatesNew() {
        TokenConvite oldToken = new TokenConvite();
        oldToken.setToken(UUID.randomUUID().toString());
        oldToken.setUser(user);

        when(tokenConviteRepository.findByUser(eq(user))).thenReturn(Optional.of(oldToken));
        when(tokenConviteRepository.save(any(TokenConvite.class))).thenReturn(tokenConvite);

        tokenConviteService.gerarEEnviarConvite(user);

        verify(tokenConviteRepository).delete(oldToken);
        verify(tokenConviteRepository).flush();
        verify(tokenConviteRepository).save(any(TokenConvite.class));
    }

    @Test
    @DisplayName("Accept invite: valid token and password updates user and deletes token")
    void aceitarConvite_validToken_acceptsAndUpdatesUser() {
        String encodedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz123456789";

        when(tokenConviteRepository.findByToken(eq(token))).thenReturn(Optional.of(tokenConvite));
        when(passwordEncoder.encode(eq(password))).thenReturn(encodedPassword);
        when(userRepository.save(eq(user))).thenReturn(user);

        tokenConviteService.aceitarConvite(token, password);

        assertEquals(encodedPassword, user.getPassword());
        assertTrue(user.getEnabled());
        verify(userRepository).save(user);
        verify(tokenConviteRepository).delete(tokenConvite);
    }

    @Test
    @DisplayName("Accept invite: invalid token throws RuntimeException")
    void aceitarConvite_invalidToken_throwsException() {
        when(tokenConviteRepository.findByToken(token)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tokenConviteService.aceitarConvite(token, password));

        assertEquals("Convite inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Accept invite: expired token throws RuntimeException")
    void aceitarConvite_expiredToken_throwsException() {
        tokenConvite.setCreatedAt(OffsetDateTime.now().minusHours(25));

        when(tokenConviteRepository.findByToken(token)).thenReturn(Optional.of(tokenConvite));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tokenConviteService.aceitarConvite(token, password));

        assertEquals("Convite expirado, solicite um novo", exception.getMessage());
    }

    @Test
    @DisplayName("Resend invite: old expired token resends new invitation")
    void reenviarConvitePorTokenAntigo_expiredToken_createsAndSendsNew() {
        tokenConvite.setCreatedAt(OffsetDateTime.now().minusHours(25));

        when(tokenConviteRepository.findByToken(token)).thenReturn(Optional.of(tokenConvite));
        when(tokenConviteRepository.findByUser(user)).thenReturn(Optional.of(tokenConvite));
        when(tokenConviteRepository.save(any(TokenConvite.class))).thenReturn(tokenConvite);

        tokenConviteService.reenviarConvitePorTokenAntigo(token);

        verify(tokenConviteRepository).delete(tokenConvite);
        verify(tokenConviteRepository).save(any(TokenConvite.class));
        verify(notificacaoService).notificarEmail(
                eq(TipoNotificacao.DEFINIR_SENHA),
                eq(user),
                any(String.class)
        );
    }

    @Test
    @DisplayName("Resend invite: valid token throws RuntimeException")
    void reenviarConvitePorTokenAntigo_validToken_throwsException() {
        tokenConvite.setCreatedAt(OffsetDateTime.now());

        when(tokenConviteRepository.findByToken(token)).thenReturn(Optional.of(tokenConvite));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tokenConviteService.reenviarConvitePorTokenAntigo(token));

        assertEquals("Este convite ainda é válido, não é necessário reenviar", exception.getMessage());
    }

    @Test
    @DisplayName("Resend invite: invalid token throws RuntimeException")
    void reenviarConvitePorTokenAntigo_invalidToken_throwsException() {
        when(tokenConviteRepository.findByToken(token)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tokenConviteService.reenviarConvitePorTokenAntigo(token));

        assertEquals("Token inválido", exception.getMessage());
    }

    @Test
    @DisplayName("Accept invite: checks token expiration before accepting")
    void aceitarConvite_tokenExpirationCheck_validatesBeforeUpdate() {
        tokenConvite.setCreatedAt(OffsetDateTime.now().minusHours(24).minusMinutes(1));

        when(tokenConviteRepository.findByToken(token)).thenReturn(Optional.of(tokenConvite));

        assertThrows(RuntimeException.class, () -> tokenConviteService.aceitarConvite(token, password));
    }

    @Test
    @DisplayName("Accept invite: token expiration threshold is 24 hours")
    void aceitarConvite_tokenBoundary_exactlyTwentyFourHoursExpires() {
        tokenConvite.setCreatedAt(OffsetDateTime.now().minusHours(24));

        when(tokenConviteRepository.findByToken(token)).thenReturn(Optional.of(tokenConvite));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> tokenConviteService.aceitarConvite(token, password));

        assertTrue(exception.getMessage().contains("Convite expirado"));
    }
}
