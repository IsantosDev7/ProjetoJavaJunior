package com.projeto.portalaluno.solicitacao;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoRequest;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoResponse;
import com.projeto.portalaluno.suporte.Chamado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public SolicitacaoService(FuncionarioRepository funcionarioRepository, SolicitacaoRepository solicitacaoRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.solicitacaoRepository = solicitacaoRepository;
    }

    private SolicitacaoResponse toResponse(Solicitacao solicitacao){
        return new SolicitacaoResponse(
                solicitacao.getId(),
                solicitacao.getTipo(),
                solicitacao.getFuncionario().getName(),  // nomeSolicitante
                solicitacao.getFuncionarioAlvoId(),
                solicitacao.getMotivo(),                  // motivo
                solicitacao.getStatus(),
                solicitacao.getCreatedAt()
        );
    }

    private void dadosSolicitacao(Solicitacao solicitacao, SolicitacaoRequest solicitacaoRequest){
        solicitacao.setTipo(solicitacaoRequest.getTipo());
        solicitacao.setMotivo(solicitacaoRequest.getMotivo());
        solicitacao.setFuncionarioAlvoId(solicitacaoRequest.getFuncionarioAlvoId());
    }

    @Transactional
    public SolicitacaoResponse criarSolicitacao(SolicitacaoRequest request, User usuarioLogado){
        Funcionario funcionario = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Somente funcionários podem criar solicitações"));

        funcionarioRepository.findById(request.getFuncionarioAlvoId())
                .orElseThrow(() -> new RuntimeException("Funcionário alvo não encontrado"));

        Solicitacao solicitacao = new Solicitacao();
        solicitacao.setFuncionario(funcionario);
        dadosSolicitacao(solicitacao, request);

        Solicitacao solicitacaoSalva = solicitacaoRepository.save(solicitacao);
        return toResponse(solicitacaoSalva);
    }

    @Transactional(readOnly = true)
    public Page<SolicitacaoResponse> listarSolicitacoes(int pagina, int tamanho, LocalDateTime inicio, LocalDateTime fim){
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Order.desc("createdAt"))
        );
        boolean temDatas = inicio != null && fim != null;
        Page<Solicitacao> page = temDatas
                ? solicitacaoRepository.findByCreatedAtBetween(inicio, fim, pageable)
                : solicitacaoRepository.findAll(pageable);

        return page.map(this::toResponse);
    }
}
