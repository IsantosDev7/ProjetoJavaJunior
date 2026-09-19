package com.projeto.portalaluno.shared.emailTest;

import com.projeto.portalaluno.shared.email.Email;
import com.projeto.portalaluno.shared.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    private Email email;

    @BeforeEach
    void setup() {
        email = new Email(
            "usuario@test.com",
            "Assunto do Email",
            "Conteúdo do email"
        );
    }

    @Test
    @DisplayName("Case 1: Send email successfully")
    void deveEnviarEmailComSucesso() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail(email));

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Case 2: Send email with valid recipient")
    void deveValidarDestinatarioDoEmail() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);

        assertNotNull(email.to());
        assertTrue(email.to().contains("@"));
    }

    @Test
    @DisplayName("Case 3: Send email with valid subject")
    void deveValidarAssuntoDoEmail() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);

        assertNotNull(email.subject());
        assertFalse(email.subject().isBlank());
    }

    @Test
    @DisplayName("Case 4: Send email with valid body")
    void deveValidarCorpoDoEmail() {
        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(email);

        assertNotNull(email.body());
        assertFalse(email.body().isBlank());
    }

    @Test
    @DisplayName("Case 5: Send email with long content")
    void deveEnviarEmailComConteudoLongo() {
        String conteudoLongo = "Lorem ipsum ".repeat(100);
        Email emailLongo = new Email("usuario@test.com", "Assunto", conteudoLongo);

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail(emailLongo));

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Case 6: Send email with special characters")
    void deveEnviarEmailComCaracteresEspeciais() {
        Email emailEspecial = new Email(
            "usuario@test.com",
            "Assunto com àáâã éê íì óô õö ú",
            "Conteúdo com çãõ"
        );

        doNothing().when(javaMailSender).send(any(SimpleMailMessage.class));

        assertDoesNotThrow(() -> emailService.sendEmail(emailEspecial));

        verify(javaMailSender).send(any(SimpleMailMessage.class));
    }
}
