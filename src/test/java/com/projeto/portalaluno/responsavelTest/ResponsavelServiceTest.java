package com.projeto.portalaluno.responsavelTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;
import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.responsavel.Responsavel;
import com.projeto.portalaluno.responsavel.ResponsavelRepository;
import com.projeto.portalaluno.responsavel.ResponsavelService;
import com.projeto.portalaluno.responsavel.StatusResponsavel;
import com.projeto.portalaluno.responsavel.dto.ResponsavelRequest;
import com.projeto.portalaluno.responsavel.dto.ResponsavelResponse;
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
public class ResponsavelServiceTest {

    @Mock
    private ResponsavelRepository responsavelRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private ResponsavelService responsavelService;

    private Responsavel responsavel;
    private ResponsavelRequest request;
    private UUID responsavelId;

    @BeforeEach
    void setup() {
        responsavelId = UUID.randomUUID();

        responsavel = new Responsavel();
        responsavel.setId(responsavelId);
        responsavel.setName("João Silva");
        responsavel.setEmail("joao@test.com");
        responsavel.setPhone("11999999999");
        responsavel.setBirthdate(LocalDate.of(1980, 5, 15));
        responsavel.setStatus(StatusResponsavel.ATIVO);

        request = new ResponsavelRequest();
        request.setName("João Silva Atualizado");
        request.setEmail("joao.novo@test.com");
        request.setPhone("11988888888");
        request.setBirthdate(LocalDate.of(1980, 5, 15));
    }

    @Test
    @DisplayName("Case 1: List responsaveis without filter")
    void deveListarResponsaveisComSucesso() {
        Page<Responsavel> page = new PageImpl<>(List.of(responsavel));

        when(responsavelRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ResponsavelResponse> resultado = responsavelService.listarResponsavel(null, 0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(responsavelRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Case 2: List responsaveis with name filter")
    void deveListarResponsaveisComFiltro() {
        String nome = "João";
        Page<Responsavel> page = new PageImpl<>(List.of(responsavel));

        when(responsavelRepository.findByNameContainingIgnoreCase(nome, any(Pageable.class))).thenReturn(page);

        Page<ResponsavelResponse> resultado = responsavelService.listarResponsavel(nome, 0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(responsavelRepository).findByNameContainingIgnoreCase(nome, any(Pageable.class));
    }

    @Test
    @DisplayName("Case 3: Update responsavel successfully")
    void deveAtualizarResponsavelComSucesso() {
        User usuario = new User();

        when(responsavelRepository.findById(responsavelId)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);

        ResponsavelResponse resultado = responsavelService.atualizarResponsavel(request, usuario, responsavelId);

        assertNotNull(resultado);
        assertEquals(request.getName(), responsavel.getName());
        verify(responsavelRepository).save(responsavel);
    }

    @Test
    @DisplayName("Case 4: Update responsavel fails - not found")
    void deveLancarExcecaoResponsavelNaoEncontrado() {
        User usuario = new User();

        when(responsavelRepository.findById(responsavelId)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            responsavelService.atualizarResponsavel(request, usuario, responsavelId);
        });

        assertEquals("Responsável inexistente com esse id", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 5: Update my responsavel successfully")
    void deveAtualizarMeuResponsavelComSucesso() {
        Aluno aluno = new Aluno();
        aluno.setResponsavel(responsavel);
        User usuario = new User();

        when(alunoRepository.findByUsuario(usuario)).thenReturn(Optional.of(aluno));
        when(responsavelRepository.findById(responsavelId)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);

        ResponsavelResponse resultado = responsavelService.atualizarMeuResponsavel(request, usuario, responsavelId);

        assertNotNull(resultado);
        verify(responsavelRepository).save(responsavel);
    }

    @Test
    @DisplayName("Case 6: Update my responsavel fails - student not found")
    void deveLancarExcecaoAlunoNaoEncontradoAoAtualizarMeu() {
        User usuario = new User();

        when(alunoRepository.findByUsuario(usuario)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            responsavelService.atualizarMeuResponsavel(request, usuario, responsavelId);
        });

        assertEquals("\"Aluno não encontrado para este usuário\"", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 7: Update my responsavel fails - different responsavel")
    void deveLancarExcecaoResponsavelNaoPertenteAoAluno() {
        Aluno aluno = new Aluno();
        Responsavel outroResponsavel = new Responsavel();
        outroResponsavel.setId(UUID.randomUUID());
        aluno.setResponsavel(outroResponsavel);

        User usuario = new User();

        when(alunoRepository.findByUsuario(usuario)).thenReturn(Optional.of(aluno));
        when(responsavelRepository.findById(responsavelId)).thenReturn(Optional.of(responsavel));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            responsavelService.atualizarMeuResponsavel(request, usuario, responsavelId);
        });

        assertEquals("Esse responsável não pertence ao aluno logado ou aluno logado não possui responsável cadastrado", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 8: Deactivate responsavel successfully")
    void deveDesativarResponsavelComSucesso() {
        when(responsavelRepository.findById(responsavelId)).thenReturn(Optional.of(responsavel));
        when(responsavelRepository.save(any(Responsavel.class))).thenReturn(responsavel);

        assertDoesNotThrow(() -> responsavelService.desativarResponsavel(responsavelId));

        assertEquals(StatusResponsavel.CANCELADO, responsavel.getStatus());
        verify(responsavelRepository).save(responsavel);
    }

    @Test
    @DisplayName("Case 9: Deactivate responsavel fails - not found")
    void deveLancarExcecaoAoDesativarResponsavelNaoEncontrado() {
        when(responsavelRepository.findById(responsavelId)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            responsavelService.desativarResponsavel(responsavelId);
        });

        assertEquals("Responsável não encontrando", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 10: Deactivate responsavel fails - already canceled")
    void deveLancarExcecaoResponsavelJaCancelado() {
        responsavel.setStatus(StatusResponsavel.CANCELADO);

        when(responsavelRepository.findById(responsavelId)).thenReturn(Optional.of(responsavel));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            responsavelService.desativarResponsavel(responsavelId);
        });

        assertEquals("Responsável já se encontra cancelado", excecao.getMessage());
    }
}
