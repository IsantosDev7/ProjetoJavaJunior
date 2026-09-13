package com.example.portalaluno.auth.tokenconvite;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.shared.email.Email;
import com.example.portalaluno.shared.email.EmailService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class TokenConviteService {

    private final TokenConviteRepository tokenConviteRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenConviteService(TokenConviteRepository tokenConviteRepository,
                               UserRepository userRepository,
                               EmailService emailService,
                               BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.tokenConviteRepository = tokenConviteRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public void gerarEEnviarConvite(User user) {
        tokenConviteRepository.findByUser(user)
                .ifPresent(tokenConviteRepository::delete);

        TokenConvite convite = new TokenConvite();
        convite.setToken(UUID.randomUUID().toString());
        convite.setUser(user);
        tokenConviteRepository.save(convite);

        String link = "https://seusistema.com/aceitar-convite?token=" + convite.getToken();
        Email email = new Email(
                user.getEmail(),
                "Bem-vindo ao Portal do Aluno - Defina sua senha",
                "Clique no link para definir sua senha: " + link
        );
        emailService.sendEmail(email);
    }

    @Transactional
    public void aceitarConvite(String token, String novaSenha) {
        TokenConvite convite = tokenConviteRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Convite inválido"));

        if (convite.isExpired()) {
            throw new RuntimeException("Convite expirado, solicite um novo");
        }

        User user = convite.getUser();
        user.setPassword(bCryptPasswordEncoder.encode(novaSenha));
        user.setEnabled(true);
        userRepository.save(user);

        tokenConviteRepository.delete(convite);
    }

    @Transactional
    public void reenviarConvite(User user) {
        gerarEEnviarConvite(user);
    }
}