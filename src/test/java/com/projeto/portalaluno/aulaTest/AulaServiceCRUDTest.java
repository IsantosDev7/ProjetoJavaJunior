package com.projeto.portalaluno.aulaTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;
import com.projeto.portalaluno.aula.Aula;
import com.projeto.portalaluno.aula.AulaRepository;
import com.projeto.portalaluno.aula.AulaService;
import com.projeto.portalaluno.aula.dto.AulaRequest;
import com.projeto.portalaluno.aula.dto.AulaResponse;
import com.projeto.portalaluno.aula.roles.Modalidade;
import com.projeto.portalaluno.aula.roles.StatusAula;
import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AulaServiceCRUDTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private AulaService aulaService;

    private User usuarioLogado;
    private Funcionario professor;
    private Aluno aluno;
    private Aula aula;
    private AulaRequest request;

    @BeforeEach
    void setup() {
        usuarioLogado = new User();
        usuarioLogado.setId(UUID.randomUUID());

        professor = new Funcionario();
        professor.setId(UUID.randomUUID());
        professor.setName("Professor João");
        professor.setUsuario(usuarioLogado);

        aluno = new Aluno();
        aluno.setId(UUID.randomUUID());
        aluno.setName("Aluno Maria");

        aula = new Aula();
        aula.setId(UUID.randomUUID());
        aula.setTitulo("Aula de Matemática");
        aula.setModalidade(Modalidade.PRESENCIAL);
        aula.setDuracaoAula(60);
        aula.setAluno(aluno);
        aula.setProfessor(professor);
        aula.setDataHoraAula(LocalDateTime.now().plusDays(1));
        aula.setStatusAula(StatusAula.PREVISTA);

        request = new AulaRequest();
        request.setTitulo("Aula de Matemática");
        request.setModalidade(Modalidade.PRESENCIAL);
        request.setDuracao(60);
        request.setAlunoId(aluno.getId());
        request.setDataHoraAula(LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("Case 1: Register aula successfully")
    void deveRegistrarAulaComSucesso() {
        when(funcionarioRepository.findByUsuario(eq(usuarioLogado))).thenReturn(Optional.of(professor));
        when(alunoRepository.findById(eq(aluno.getId()))).thenReturn(Optional.of(aluno));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        AulaResponse resultado = aulaService.cadastrarAula(request, usuarioLogado);

        assertNotNull(resultado);
        assertEquals(request.getTitulo(), resultado.getTitulo());
        verify(aulaRepository).save(any(Aula.class));
    }

    @Test
    @DisplayName("Case 2: Register aula fails - user is not funcionario")
    void deveLancarExcecaoUsuarioNaoEhFuncionario() {
        when(funcionarioRepository.findByUsuario(eq(usuarioLogado))).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            aulaService.cadastrarAula(request, usuarioLogado);
        });

        assertEquals("Usuário logado não é um funcionário", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 3: Register aula fails - student not found")
    void deveLancarExcecaoAlunoNaoEncontrado() {
        when(funcionarioRepository.findByUsuario(eq(usuarioLogado))).thenReturn(Optional.of(professor));
        when(alunoRepository.findById(eq(aluno.getId()))).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            aulaService.cadastrarAula(request, usuarioLogado);
        });

        assertEquals("Aluno não encontrado", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 4: List my classes successfully")
    void deveListarMinhasAulasComSucesso() {
        Page<Aula> page = new PageImpl<>(List.of(aula));

        when(alunoRepository.findByUsuario(eq(usuarioLogado))).thenReturn(Optional.of(aluno));
        when(aulaRepository.findByAlunoId(eq(aluno.getId()), any(Pageable.class))).thenReturn(page);

        Page<AulaResponse> resultado = aulaService.minhaAulas(usuarioLogado, 0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }

    @Test
    @DisplayName("Case 5: List classes fails - student profile not found")
    void deveLancarExcecaoAoProcurarPerfilAluno() {
        when(alunoRepository.findByUsuario(eq(usuarioLogado))).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            aulaService.minhaAulas(usuarioLogado, 0, 10);
        });

        assertEquals("O usuário logado não possui um perfil de aluno vinculado.", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 6: Update aula successfully")
    void deveAtualizarAulaComSucesso() {
        UUID aulaId = aula.getId();

        when(funcionarioRepository.findByUsuario(eq(usuarioLogado))).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(eq(aulaId))).thenReturn(Optional.of(aula));
        when(alunoRepository.findById(eq(request.getAlunoId()))).thenReturn(Optional.of(aluno));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        AulaResponse resultado = aulaService.atualizarAula(aulaId, request, usuarioLogado);

        assertNotNull(resultado);
        verify(aulaRepository).save(aula);
    }

    @Test
    @DisplayName("Case 7: Update aula fails - aula not found")
    void deveLancarExcecaoAulaNaoEncontradaAoAtualizar() {
        UUID aulaId = UUID.randomUUID();

        when(funcionarioRepository.findByUsuario(eq(usuarioLogado))).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(eq(aulaId))).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            aulaService.atualizarAula(aulaId, request, usuarioLogado);
        });

        assertEquals("Aula inexistente", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 8: Cancel aula successfully")
    void deveCancelarAulaComSucesso() {
        UUID aulaId = aula.getId();

        when(aulaRepository.findById(eq(aulaId))).thenReturn(Optional.of(aula));
        when(aulaRepository.save(any(Aula.class))).thenReturn(aula);

        aulaService.cancelarAula(aulaId);

        assertEquals(StatusAula.CANCELADA, aula.getStatusAula());
        verify(aulaRepository).save(aula);
    }

    @Test
    @DisplayName("Case 9: Cancel aula fails - aula not found")
    void deveLancarExcecaoAulaNaoEncontradaAoCancelar() {
        UUID aulaId = UUID.randomUUID();

        when(aulaRepository.findById(eq(aulaId))).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            aulaService.cancelarAula(aulaId);
        });

        assertEquals("Aula inexistente", excecao.getMessage());
    }
}
