package com.projeto.portalaluno.shared.notificacao;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.shared.email.EmailService;
import org.springframework.stereotype.Service;
import com.projeto.portalaluno.shared.email.Email;

@Service
public class NotificacaoService{

    private final EmailService emailService;

    public NotificacaoService(EmailService emailService) {
        this.emailService = emailService;
    }


    public void notificarEmail(TipoNotificacao tipo, User user, String link) {
        String conteudo = tipo.getBody();
        if (link != null && !link.isBlank()) {
            conteudo += " " + link;
        }

        Email email = new Email(
            user.getEmail(),
            tipo.getSubject(),
            conteudo
        );
        emailService.sendEmail(email);
    }
}
