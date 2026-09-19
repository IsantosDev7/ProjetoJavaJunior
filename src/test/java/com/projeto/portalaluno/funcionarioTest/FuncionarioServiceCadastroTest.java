package com.projeto.portalaluno.funcionarioTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRepository;
import com.projeto.portalaluno.auth.UserRole;
import com.projeto.portalaluno.cargo.Cargo;
import com.projeto.portalaluno.cargo.CargoRepository;
import com.projeto.portalaluno.auth.tokenconvite.TokenConviteService;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.funcionario.FuncionarioService;
import com.projeto.portalaluno.funcionario.dto.FuncionarioRequest;
import com.projeto.portalaluno.funcionario.dto.FuncionarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioServiceCadastroTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private TokenConviteService tokenConviteService;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private FuncionarioRequest request;
    private List<String> nomesDosCargos;
    private Cargo cargo;

    @BeforeEach
    void setup() {
        request = new FuncionarioRequest();
        request.setEmail("funcionario@test.com");
        request.setName("João Funcionário");
        request.setCpf("12345678901");
        request.setPhone("11999999999");
        request.setBirthDate(LocalDate.of(1990, 5, 15));
        request.setAddress("Rua teste");
        request.setCep("12345000");
        request.setCity("São Paulo");
        request.setState("SP");
        request.setCountry("Brasil");

        nomesDosCargos = List.of("PROFESSOR", "COORDENADOR");

        cargo = new Cargo();
        cargo.setId(UUID.randomUUID());
        cargo.setName("PROFESSOR");
    }

    @Test
    @DisplayName("Case 1: Register funcionario successfully")
    void deveRegistrarFuncionarioComSucesso() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cargoRepository.findByName("PROFESSOR")).thenReturn(Optional.of(cargo));
        when(cargoRepository.findByName("COORDENADOR")).thenReturn(Optional.of(cargo));
        when(funcionarioRepository.saveAndFlush(any(Funcionario.class))).thenAnswer(invocation -> {
            Funcionario f = invocation.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });

        FuncionarioResponse resultado = funcionarioService.cadastrarFuncionario(request, nomesDosCargos);

        assertNotNull(resultado);
        assertEquals(request.getName(), resultado.getName());
        assertEquals(request.getEmail(), resultado.getEmail());
        verify(userRepository).save(any(User.class));
        verify(funcionarioRepository).saveAndFlush(any(Funcionario.class));
        verify(tokenConviteService).gerarEEnviarConvite(any(User.class));
    }

    @Test
    @DisplayName("Case 2: Register funcionario fails - email already exists")
    void deveLancarExcecaoEmailJaExiste() {
        User usuarioExistente = new User();

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(usuarioExistente));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            funcionarioService.cadastrarFuncionario(request, nomesDosCargos);
        });

        assertEquals("Já existe um funcionário cadastrado com este e-mail.", excecao.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Case 3: Register funcionario fails - invalid cargo")
    void deveLancarExcecaoCargoInvalido() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cargoRepository.findByName("PROFESSOR")).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            funcionarioService.cadastrarFuncionario(request, nomesDosCargos);
        });

        assertTrue(excecao.getMessage().contains("Cargo inválido: PROFESSOR"));
    }

    @Test
    @DisplayName("Case 4: Verify user role is FUNCIONARIO when registering")
    void deveVerificarRoleDoUsuario() {
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cargoRepository.findByName(anyString())).thenReturn(Optional.of(cargo));
        when(funcionarioRepository.saveAndFlush(any(Funcionario.class))).thenAnswer(invocation -> {
            Funcionario f = invocation.getArgument(0);
            f.setId(UUID.randomUUID());
            return f;
        });

        funcionarioService.cadastrarFuncionario(request, nomesDosCargos);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User usuarioSalvo = userCaptor.getValue();
        assertEquals(UserRole.FUNCIONARIO, usuarioSalvo.getRole());
        assertNull(usuarioSalvo.getPassword());
    }
}
