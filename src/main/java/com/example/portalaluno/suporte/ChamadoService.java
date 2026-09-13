package com.example.portalaluno.suporte;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import com.example.portalaluno.suporte.dto.ChamadoRequest;
import com.example.portalaluno.suporte.dto.ChamadoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
public class ChamadoService{

    private final ChamadoRepository chamadoRepository;
    private final AlunoRepository alunoRepository;

    public ChamadoService(ChamadoRepository chamadoRepository, AlunoRepository alunoRepository, FuncionarioRepository funcionarioRepository) {
        this.chamadoRepository = chamadoRepository;
        this.alunoRepository = alunoRepository;
    }

    private ChamadoResponse toResponse(Chamado chamado){
        return new ChamadoResponse(
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getAlunoId().getId(),
                chamado.getPrioridade(),
                chamado.getCreatedAt(),
                chamado.isResolvido()
        );
    }

    @Transactional
    public ChamadoResponse criarChamado(ChamadoRequest chamadoRequest, User usuarioLogado){
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(()-> new RuntimeException("Somente alunos podem criar chamados."));

        Chamado chamado = new Chamado();
        chamado.setTitulo(chamadoRequest.getTitulo());
        chamado.setDescricao(chamadoRequest.getDescricao());
        chamado.setAlunoId(aluno);

        Chamado chamadoSalvo = chamadoRepository.save(chamado);
        return toResponse(chamadoSalvo);
    }

    @Transactional(readOnly = true)
    public Page<ChamadoResponse> listarChamados(int pagina, int tamanho, LocalDateTime inicio, LocalDateTime fim){
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Order.desc("createdAt"))
        );

        boolean temDatas = inicio != null && fim != null;
        Page<Chamado> page = temDatas
                ? chamadoRepository.findByCreatedAtBetween(inicio, fim, pageable)
                : chamadoRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    @Transactional
    public void resolverChamado(UUID chamadoId){
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new RuntimeException("Chamado não encontrado."));

        chamado.setResolvido(true);
        chamado.setUpdatedAt(LocalDateTime.now());
        chamadoRepository.save(chamado);
    }
}
