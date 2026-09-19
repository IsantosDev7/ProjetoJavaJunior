package com.projeto.portalaluno.shared.notificacaoTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.shared.email.Email;
import com.projeto.portalaluno.shared.email.EmailService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificacaoServiceTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificacaoService notificacaoService;

    private User usuario;

    @BeforeEach
    void setup() {
        usuario = new User();
        usuario.setEmail("usuario@test.com");
    }

    @Test
    @DisplayName("Case 1: Send notification email with link successfully")
    void deveNotificarEmailComLinkComSucesso() {
        String link = "https://portal-aluno.com/aceitar-convite?token=abc123";

        notificacaoService.notificarEmail(TipoNotificacao.DEFINIR_SENHA, usuario, link);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendEmail(emailCaptor.capture());

        Email emailEnviado = emailCaptor.getValue();
        assertEquals(usuario.getEmail(), emailEnviado.to());
        assertEquals(TipoNotificacao.DEFINIR_SENHA.getSubject(), emailEnviado.subject());
        assertTrue(emailEnviado.body().contains(link));
    }

    @Test
    @DisplayName("Case 2: Send notification email without link successfully")
    void deveNotificarEmailSemLinkComSucesso() {
        notificacaoService.notificarEmail(TipoNotificacao.SOLICITACAO_CRIADA, usuario, null);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendEmail(emailCaptor.capture());

        Email emailEnviado = emailCaptor.getValue();
        assertEquals(usuario.getEmail(), emailEnviado.to());
        assertEquals(TipoNotificacao.SOLICITACAO_CRIADA.getSubject(), emailEnviado.subject());
        assertEquals(TipoNotificacao.SOLICITACAO_CRIADA.getBody(), emailEnviado.body());
    }

    @Test
    @DisplayName("Case 3: Send notification email with blank link")
    void deveNotificarEmailComLinkEmBrancoComSucesso() {
        notificacaoService.notificarEmail(TipoNotificacao.SOLICITACAO_CRIADA, usuario, "   ");

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendEmail(emailCaptor.capture());

        Email emailEnviado = emailCaptor.getValue();
        assertEquals(usuario.getEmail(), emailEnviado.to());
        assertEquals(TipoNotificacao.SOLICITACAO_CRIADA.getBody(), emailEnviado.body());
    }

    @Test
    @DisplayName("Case 4: Send different notification types")
    void deveEnviarDiferentesTiposDeNotificacao() {
        TipoNotificacao[] tipos = TipoNotificacao.values();

        for (TipoNotificacao tipo : tipos) {
            notificacaoService.notificarEmail(tipo, usuario, null);
        }

        verify(emailService).sendEmail(any(Email.class));
    }

    @Test
    @DisplayName("Case 5: Notification content uses correct subject and body")
    void deveVerificarConteudoDaNotificacao() {
        String link = "https://example.com";

        notificacaoService.notificarEmail(TipoNotificacao.DEFINIR_SENHA, usuario, link);

        ArgumentCaptor<Email> emailCaptor = ArgumentCaptor.forClass(Email.class);
        verify(emailService).sendEmail(emailCaptor.capture());

        Email emailEnviado = emailCaptor.getValue();
        assertNotNull(emailEnviado.subject());
        assertNotNull(emailEnviado.body());
        assertFalse(emailEnviado.subject().isBlank());
        assertFalse(emailEnviado.body().isBlank());
    }
}
