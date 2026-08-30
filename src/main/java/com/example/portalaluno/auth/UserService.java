package com.example.portalaluno.auth;

import com.example.portalaluno.shared.TokenService;
import com.example.portalaluno.shared.exception.ContaInativaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import do encoder de senha
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Injetamos o encoder para checar a senha criptografada

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String login(String email, String password) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail ou senha estão incorretos."));

        if (!user.getEnabled()) {
            throw new ContaInativaException("Acesso negado: Seu cadastro ainda está aguardando aprovação da secretaria.");
        }

        boolean senhaConfere = passwordEncoder.matches(password, user.getPassword());

        if (!senhaConfere) {
            throw new RuntimeException("E-mail ou senha estão incorretos.");
        }

        String token = tokenService.geraToken(user);
        return token;
    }
}