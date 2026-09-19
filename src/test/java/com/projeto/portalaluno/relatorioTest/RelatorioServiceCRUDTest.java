package com.projeto.portalaluno.relatorioTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;
import com.projeto.portalaluno.aula.Aula;
import com.projeto.portalaluno.aula.AulaRepository;
import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.relatorio.Relatorio;
import com.projeto.portalaluno.relatorio.RelatorioRepository;
import com.projeto.portalaluno.relatorio.RelatorioService;
import com.projeto.portalaluno.relatorio.dto.RelatorioRequest;
import com.projeto.portalaluno.relatorio.dto.RelatorioResponse;
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
public class RelatorioServiceCRUDTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private RelatorioRepository relatorioRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private RelatorioService relatorioService;

    private User usuarioLogado;
    private Funcionario professor;
    private Aula aula;
    private Relatorio relatorio;
    private RelatorioRequest request;

    @BeforeEach
    void setup() {
        usuarioLogado = new User();
        usuarioLogado.setId(UUID.randomUUID());

        professor = new Funcionario();
        professor.setId(UUID.randomUUID());
        professor.setName("Professor João");
        professor.setUsuario(usuarioLogado);

        aula = new Aula();
        aula.setId(UUID.randomUUID());
        aula.setTitulo("Aula de Matemática");
        aula.setDataHoraAula(LocalDateTime.now().minusHours(1));

        relatorio = new Relatorio();
        relatorio.setId(UUID.randomUUID());
        relatorio.setAula(aula);
        relatorio.setProfessor(professor);
        relatorio.setTexto("Conteúdo do relatório");
        relatorio.setLido(false);
        relatorio.setCreatedAt(LocalDateTime.now());

        request = new RelatorioRequest();
        request.setAulaid(aula.getId());
        request.setTexto("Conteúdo do relatório");
    }

    @Test
    @DisplayName("Case 1: Create relatorio successfully")
    void deveCriarRelatorioComSucesso() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(relatorioRepository.existsByAulaId(aula.getId())).thenReturn(false);
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        RelatorioResponse resultado = relatorioService.criarRelatorio(usuarioLogado, request);

        assertNotNull(resultado);
        assertEquals(request.getTexto(), resultado.getTexto());
        verify(relatorioRepository).save(any(Relatorio.class));
    }

    @Test
    @DisplayName("Case 2: Create relatorio with specified professor")
    void deveCriarRelatorioComProfessorEspecifico() {
        request.setProfessorId(professor.getId());

        when(funcionarioRepository.findById(professor.getId())).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(relatorioRepository.existsByAulaId(aula.getId())).thenReturn(false);
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        RelatorioResponse resultado = relatorioService.criarRelatorio(usuarioLogado, request);

        assertNotNull(resultado);
        verify(relatorioRepository).save(any(Relatorio.class));
    }

    @Test
    @DisplayName("Case 3: Create relatorio fails - professor not found")
    void deveLancarExcecaoProfessorNaoEncontrado() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            relatorioService.criarRelatorio(usuarioLogado, request);
        });

        assertEquals("Usuário logado não é um funcionário", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 4: Create relatorio fails - aula not found")
    void deveLancarExcecaoAulaNaoEncontrada() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            relatorioService.criarRelatorio(usuarioLogado, request);
        });

        assertEquals("Aula não encontrada", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 5: Create relatorio fails - relatorio already exists for this aula")
    void deveLancarExcecaoRelatorioJaExiste() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(aulaRepository.findById(aula.getId())).thenReturn(Optional.of(aula));
        when(relatorioRepository.existsByAulaId(aula.getId())).thenReturn(true);

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            relatorioService.criarRelatorio(usuarioLogado, request);
        });

        assertEquals("Já existe um relatório para esse aula", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 6: Update relatorio successfully")
    void deveAtualizarRelatorioComSucesso() {
        UUID relatorioId = relatorio.getId();
        String novoTexto = "Novo conteúdo do relatório";
        request.setTexto(novoTexto);

        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.of(relatorio));
        when(relatorioRepository.save(any(Relatorio.class))).thenReturn(relatorio);

        RelatorioResponse resultado = relatorioService.atualizarTextoRelatorio(usuarioLogado, request, relatorioId);

        assertNotNull(resultado);
        assertEquals(novoTexto, relatorio.getTexto());
        verify(relatorioRepository).save(relatorio);
    }

    @Test
    @DisplayName("Case 7: Update relatorio fails - relatorio not found")
    void deveLancarExcecaoRelatorioNaoEncontradoAoAtualizar() {
        UUID relatorioId = UUID.randomUUID();

        when(relatorioRepository.findById(relatorioId)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            relatorioService.atualizarTextoRelatorio(usuarioLogado, request, relatorioId);
        });

        assertEquals("Relatório não encontrado", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 8: List my relatorios successfully")
    void deveListarMeusRelatoriosComSucesso() {
        Page<Relatorio> page = new PageImpl<>(List.of(relatorio));

        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(professor));
        when(relatorioRepository.findByProfessorId(eq(professor.getId()), any(Pageable.class))).thenReturn(page);

        Page<RelatorioResponse> resultado = relatorioService.meusRelatorios(usuarioLogado, 0, 10, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
    }
}
