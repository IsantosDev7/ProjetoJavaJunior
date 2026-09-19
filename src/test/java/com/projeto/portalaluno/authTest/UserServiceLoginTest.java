package com.projeto.portalaluno.authTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRepository;
import com.projeto.portalaluno.auth.UserService;
import com.projeto.portalaluno.shared.exception.ContaInativaException;
import com.projeto.portalaluno.shared.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceLoginTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("Case 1: Login successful with valid credentials")
    void deveFazerLoginComSucesso() {
        String email = "usuario@test.com";
        String senha = "senha123";
        String senhaCriptografada = "$2a$10$abc123";
        String tokenEsperado = "jwt-token-123";

        User usuario = new User();
        usuario.setEmail(email);
        usuario.setPassword(senhaCriptografada);
        usuario.setEnabled(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senha, senhaCriptografada)).thenReturn(true);
        when(tokenService.geraToken(usuario)).thenReturn(tokenEsperado);

        String resultado = userService.login(email, senha);

        assertEquals(tokenEsperado, resultado);
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(senha, senhaCriptografada);
        verify(tokenService).geraToken(usuario);
    }

    @Test
    @DisplayName("Case 2: Login fails - user not found")
    void deveLancarExcecaoUsuarioNaoEncontrado() {
        String email = "naoexiste@test.com";
        String senha = "senha123";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            userService.login(email, senha);
        });

        assertEquals("E-mail ou senha estão incorretos.", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 3: Login fails - account disabled")
    void deveLancarExcecaoContaInativa() {
        String email = "usuario@test.com";
        String senha = "senha123";

        User usuario = new User();
        usuario.setEmail(email);
        usuario.setPassword("senha123");
        usuario.setEnabled(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        ContaInativaException excecao = assertThrows(ContaInativaException.class, () -> {
            userService.login(email, senha);
        });

        assertEquals("Acesso negado: Seu cadastro ainda está aguardando aprovação da secretaria.", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 4: Login fails - wrong password")
    void deveLancarExcecaoSenhaIncorreta() {
        String email = "usuario@test.com";
        String senhaInformada = "senhaErrada";
        String senhaCriptografada = "$2a$10$abc123";

        User usuario = new User();
        usuario.setEmail(email);
        usuario.setPassword(senhaCriptografada);
        usuario.setEnabled(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(senhaInformada, senhaCriptografada)).thenReturn(false);

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            userService.login(email, senhaInformada);
        });

        assertEquals("E-mail ou senha estão incorretos.", excecao.getMessage());
    }
}
