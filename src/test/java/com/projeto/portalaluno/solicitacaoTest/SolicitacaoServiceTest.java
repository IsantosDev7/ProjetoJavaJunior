package com.projeto.portalaluno.solicitacaoTest;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRepository;
import com.projeto.portalaluno.auth.UserRole;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.shared.notificacao.NotificacaoService;
import com.projeto.portalaluno.shared.notificacao.TipoNotificacao;
import com.projeto.portalaluno.solicitacao.Solicitacao;
import com.projeto.portalaluno.solicitacao.SolicitacaoRepository;
import com.projeto.portalaluno.solicitacao.SolicitacaoService;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoRequest;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoResponse;
import com.projeto.portalaluno.solicitacao.status.SolicitacaoStatus;
import com.projeto.portalaluno.solicitacao.status.SolicitacaoTipo;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SolicitacaoServiceTest {

    @Mock
    private SolicitacaoRepository solicitacaoRepository;

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SolicitacaoService solicitacaoService;

    private User usuarioLogado;
    private User admin;
    private Funcionario funcionario;
    private Funcionario funcionarioAlvo;
    private SolicitacaoRequest request;

    @BeforeEach
    void setup() {
        usuarioLogado = new User();
        usuarioLogado.setId(UUID.randomUUID());
        usuarioLogado.setEmail("funcionario@test.com");

        admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setEmail("admin@test.com");
        admin.setRole(UserRole.SUPER_ADMIN);

        funcionario = new Funcionario();
        funcionario.setId(UUID.randomUUID());
        funcionario.setName("João Funcionário");
        funcionario.setUsuario(usuarioLogado);

        funcionarioAlvo = new Funcionario();
        funcionarioAlvo.setId(UUID.randomUUID());
        funcionarioAlvo.setName("Maria Funcionária");

        request = new SolicitacaoRequest();
        request.setTipo(SolicitacaoTipo.AFASTAMENTO);
        request.setMotivo("Motivo da solicitação");
        request.setFuncionarioAlvoId(funcionarioAlvo.getId());
    }

    @Test
    @DisplayName("Case 1: Create solicitacao successfully")
    void deveCriarSolicitacaoComSucesso() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.findById(funcionarioAlvo.getId())).thenReturn(Optional.of(funcionarioAlvo));
        when(userRepository.findByRole(UserRole.SUPER_ADMIN)).thenReturn(Optional.of(admin));
        when(solicitacaoRepository.save(any(Solicitacao.class))).thenAnswer(invocation -> {
            Solicitacao s = invocation.getArgument(0);
            s.setId(UUID.randomUUID());
            return s;
        });

        SolicitacaoResponse resultado = solicitacaoService.criarSolicitacao(request, usuarioLogado);

        assertNotNull(resultado);
        assertEquals(request.getTipo(), resultado.getTipo());
        assertEquals(request.getMotivo(), resultado.getMotivo());
        verify(solicitacaoRepository).save(any(Solicitacao.class));
        verify(notificacaoService, times(2)).notificarEmail(eq(TipoNotificacao.SOLICITACAO_CRIADA), any(User.class), isNull());
    }

    @Test
    @DisplayName("Case 2: Create solicitacao fails - user is not funcionario")
    void deveLancarExcecaoUsuarioNaoEhFuncionario() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.criarSolicitacao(request, usuarioLogado);
        });

        assertEquals("Somente funcionários podem criar solicitações", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 3: Create solicitacao fails - target funcionario not found")
    void deveLancarExcecaoFuncionarioAlvoNaoEncontrado() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.findById(funcionarioAlvo.getId())).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.criarSolicitacao(request, usuarioLogado);
        });

        assertEquals("Funcionário alvo não encontrado", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 4: Create solicitacao fails - admin not found")
    void deveLancarExcecaoAdminNaoEncontrado() {
        when(funcionarioRepository.findByUsuario(usuarioLogado)).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.findById(funcionarioAlvo.getId())).thenReturn(Optional.of(funcionarioAlvo));
        when(userRepository.findByRole(UserRole.SUPER_ADMIN)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            solicitacaoService.criarSolicitacao(request, usuarioLogado);
        });

        assertEquals("Administrador não encontrado no sistema", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 5: List solicitacoes without date filter")
    void deveListarSolicitacoesComSucesso() {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(UUID.randomUUID());
        solicitacao.setTipo(SolicitacaoTipo.AFASTAMENTO);
        solicitacao.setFuncionario(funcionario);
        solicitacao.setStatus(SolicitacaoStatus.PENDENTE);

        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));

        when(solicitacaoRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<SolicitacaoResponse> resultado = solicitacaoService.listarSolicitacoes(0, 10, null, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(solicitacaoRepository).findAll(any(Pageable.class));
    }

    @Test
    @DisplayName("Case 6: List solicitacoes with date filter")
    void deveListarSolicitacoesComFiltroData() {
        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setId(UUID.randomUUID());
        solicitacao.setTipo(SolicitacaoTipo.AFASTAMENTO);
        solicitacao.setFuncionario(funcionario);
        solicitacao.setStatus(SolicitacaoStatus.PENDENTE);

        Page<Solicitacao> page = new PageImpl<>(List.of(solicitacao));

        LocalDateTime inicio = LocalDateTime.now().minusDays(10);
        LocalDateTime fim = LocalDateTime.now().plusDays(10);

        when(solicitacaoRepository.findByCreatedAtBetween(inicio, fim, any(Pageable.class))).thenReturn(page);

        Page<SolicitacaoResponse> resultado = solicitacaoService.listarSolicitacoes(0, 10, inicio, fim);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(solicitacaoRepository).findByCreatedAtBetween(inicio, fim, any(Pageable.class));
    }
}
