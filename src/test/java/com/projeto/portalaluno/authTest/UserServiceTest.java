package com.projeto.portalaluno.authTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRepository;
import com.projeto.portalaluno.auth.UserRole;
import com.projeto.portalaluno.auth.UserService;
import com.projeto.portalaluno.shared.exception.ContaInativaException;
import com.projeto.portalaluno.shared.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserService userService;

    private User user;
    private String email;
    private String password;
    private String hashedPassword;

    @BeforeEach
    void setup() {
        email = "test@example.com";
        password = "SecurePassword123";
        hashedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz123456789";

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPassword(hashedPassword);
        user.setRole(UserRole.ALUNO);
        user.setEnabled(true);
    }

    @Test
    @DisplayName("Login success: valid email and password returns JWT token")
    void login_validCredentials_returnsToken() {
        String expectedToken = "jwt.token.here";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(tokenService.geraToken(user)).thenReturn(expectedToken);

        String token = userService.login(email, password);

        assertEquals(expectedToken, token);
    }

    @Test
    @DisplayName("Login failure: non-existent email throws RuntimeException")
    void login_nonExistentEmail_throwsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(email, password));

        assertEquals("E-mail ou senha estão incorretos.", exception.getMessage());
    }

    @Test
    @DisplayName("Login failure: incorrect password throws RuntimeException")
    void login_incorrectPassword_throwsException() {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.login(email, password));

        assertEquals("E-mail ou senha estão incorretos.", exception.getMessage());
    }

    @Test
    @DisplayName("Login failure: inactive account throws ContaInativaException")
    void login_inactiveAccount_throwsContaInativaException() {
        user.setEnabled(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        ContaInativaException exception = assertThrows(ContaInativaException.class,
                () -> userService.login(email, password));

        assertEquals("Acesso negado: Seu cadastro ainda está aguardando aprovação da secretaria.",
                exception.getMessage());
    }

    @Test
    @DisplayName("Login success: funcionário with valid credentials")
    void login_funcionarioValidCredentials_returnsToken() {
        user.setRole(UserRole.FUNCIONARIO);
        String expectedToken = "jwt.funcionario.token";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(tokenService.geraToken(user)).thenReturn(expectedToken);

        String token = userService.login(email, password);

        assertEquals(expectedToken, token);
    }

    @Test
    @DisplayName("Login success: super_admin with valid credentials")
    void login_superAdminValidCredentials_returnsToken() {
        user.setRole(UserRole.SUPER_ADMIN);
        String expectedToken = "jwt.admin.token";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(tokenService.geraToken(user)).thenReturn(expectedToken);

        String token = userService.login(email, password);

        assertEquals(expectedToken, token);
    }

    @Test
    @DisplayName("Login validation: inactive status is checked before password validation")
    void login_checkEnabledBeforePassword_throwsContaInativaException() {
        user.setEnabled(false);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        assertThrows(ContaInativaException.class, () -> userService.login(email, password));
    }
}
