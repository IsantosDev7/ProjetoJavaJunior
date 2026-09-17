package com.projeto.portalaluno.solicitacao;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.funcionario.FuncionarioRepository;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoRequest;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}
