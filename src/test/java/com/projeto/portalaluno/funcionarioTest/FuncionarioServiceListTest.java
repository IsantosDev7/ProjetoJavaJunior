package com.projeto.portalaluno.funcionarioTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.funcionario.FuncionarioService;
import com.projeto.portalaluno.funcionario.dto.FuncionarioRequest;
import com.projeto.portalaluno.funcionario.dto.FuncionarioResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FuncionarioServiceListTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private FuncionarioService funcionarioService;

    private Funcionario funcionario;
    private FuncionarioRequest request;

    @BeforeEach
    void setup() {
        User usuario = new User();
        usuario.setId(UUID.randomUUID());
        usuario.setEmail("funcionario@test.com");

        funcionario = new Funcionario();
        funcionario.setId(UUID.randomUUID());
        funcionario.setName("João Funcionário");
        funcionario.setCpf("12345678901");
        funcionario.setPhone("11999999999");
        funcionario.setBirthDate(LocalDate.of(1990, 5, 15));
        funcionario.setAddress("Rua teste");
        funcionario.setCep("12345000");
        funcionario.setCity("São Paulo");
        funcionario.setState("SP");
        funcionario.setCountry("Brasil");
        funcionario.setUsuario(usuario);

        request = new FuncionarioRequest();
        request.setEmail("funcionario@test.com");
        request.setName("João Atualizado");
        request.setCpf("12345678901");
        request.setPhone("11988888888");
        request.setBirthDate(LocalDate.of(1990, 5, 15));
        request.setAddress("Rua atualizada");
        request.setCep("54321000");
        request.setCity("Rio de Janeiro");
        request.setState("RJ");
        request.setCountry("Brasil");
    }

    @Test
    @DisplayName("Case 1: List funcionarios without filter")
    void deveListarFuncionariosComSucesso() {
        Page<Funcionario> page = new PageImpl<>(List.of(funcionario));

        when(funcionarioRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<FuncionarioResponse> resultado = funcionarioService.listarFuncionarios(null, 0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(funcionarioRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Case 2: List funcionarios with name filter")
    void deveListarFuncionariosComFiltroNome() {
        String nome = "João";
        Page<Funcionario> page = new PageImpl<>(List.of(funcionario));

        when(funcionarioRepository.findByNameContainingIgnoreCase(nome, any(Pageable.class))).thenReturn(page);

        Page<FuncionarioResponse> resultado = funcionarioService.listarFuncionarios(nome, 0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(funcionarioRepository).findByNameContainingIgnoreCase(nome, any(Pageable.class));
    }

    @Test
    @DisplayName("Case 3: Get funcionario by ID successfully")
    void deveObterFuncionarioPorIdComSucesso() {
        UUID funcionarioId = funcionario.getId();

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.of(funcionario));

        FuncionarioResponse resultado = funcionarioService.obterFuncionarioPorId(funcionarioId);

        assertNotNull(resultado);
        assertEquals(funcionario.getName(), resultado.getName());
        verify(funcionarioRepository).findById(funcionarioId);
    }

    @Test
    @DisplayName("Case 4: Get funcionario fails - not found")
    void deveLancarExcecaoFuncionarioNaoEncontrado() {
        UUID funcionarioId = UUID.randomUUID();

        when(funcionarioRepository.findById(funcionarioId)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            funcionarioService.obterFuncionarioPorId(funcionarioId);
        });

        assertEquals("Funcionário não encontrado", excecao.getMessage());
    }
}
