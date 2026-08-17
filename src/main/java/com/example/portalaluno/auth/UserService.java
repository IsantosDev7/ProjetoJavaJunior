package com.example.portalaluno.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import do encoder de senha
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Injetamos o encoder para checar a senha criptografada

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String email, String password) {

        // Busca o usuário pelo e-mail. Se não achar, já dispara a exceção de erro genérica.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("E-mail ou senha estão incorretos."));

        // Compara a senha digitada em texto puro com o hash criptografado salvo no banco
        boolean senhaConfere = passwordEncoder.matches(password, user.getPassword());

        if (!senhaConfere) {
            throw new RuntimeException("E-mail ou senha estão incorretos.");
        }

        // Se der certo, retorna o usuário logado!
        return user;
    }
}