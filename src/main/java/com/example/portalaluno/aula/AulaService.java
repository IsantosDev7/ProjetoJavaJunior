package com.example.portalaluno.aula;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.aula.dto.AulaRequest;
import com.example.portalaluno.aula.dto.AulaResponse;
import com.example.portalaluno.aula.roles.StatusAula;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private final AlunoRepository alunoRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AulaService(AlunoRepository alunoRepository, FuncionarioRepository funcionarioRepository, AulaRepository aulaRepository) {
        this.alunoRepository = alunoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.aulaRepository = aulaRepository;
    }

    private AulaResponse toResponse(Aula aula) {
        return new AulaResponse(
                aula.getTitulo(),
                aula.getModalidade(),
                aula.getDuracaoAula(),
                aula.getAluno().getId(),
                aula.getDataHoraAula()
        );
    }

    private void AtualizarDadosAula(Aula aula, AulaRequest dadosAula) {
        aula.setTitulo(dadosAula.getTitulo());
        aula.setModalidade(dadosAula.getModalidade());
        aula.setDuracaoAula(dadosAula.getDuracao());
        aula.setDataHoraAula(dadosAula.getDataHoraAula());
        aula.setStatusAula(StatusAula.PREVISTA);
    }

    @Transactional
    public AulaResponse cadastrarAula(AulaRequest dadosAula, User usuarioLogado) {

        Funcionario professor = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));

        Aluno aluno = alunoRepository.findById(dadosAula.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Aula novaAula = new Aula();
        novaAula.setAluno(aluno);
        novaAula.setProfessor(professor);
        AtualizarDadosAula(novaAula, dadosAula);

        Aula aulaSalva = aulaRepository.save(novaAula);
        return toResponse(aulaSalva);
    }

    @Transactional(readOnly = true)
    public Page<AulaResponse> minhaAulas(User usuarioLogado, int pagina, int tamanho) {
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("O usuário logado não possui um perfil de aluno vinculado."));
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Direction.DESC, "dataHoraAula")
        );
        return aulaRepository.findByAlunoId(aluno.getId(), pageable)
                .map(aula -> new AulaResponse(
                        aula.getTitulo(),
                        aula.getModalidade(),
                        aula.getDuracaoAula(),
                        aula.getAluno().getId(),
                        aula.getDataHoraAula()
                ));
    }

    @Transactional(readOnly = true)
    public Page<AulaResponse> listarAulasAlunos(UUID alunoId, int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Direction.DESC, "dataHoraAula")
                );
        return aulaRepository.findByAlunoId(alunoId, pageable)
                .map(aula -> new AulaResponse(
                        aula.getTitulo(),
                        aula.getModalidade(),
                        aula.getDuracaoAula(),
                        aula.getAluno().getId(),
                        aula.getDataHoraAula()
                ));
    }

    @Transactional(readOnly = true)
    public Page<AulaResponse> listarAulasProfessor(UUID professorId,  int pagina, int tamanho) {
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Direction.DESC, "dataHoraAula")
        );
        return aulaRepository.findByProfessorId(professorId, pageable)
                .map(aula -> new AulaResponse(
                        aula.getTitulo(),
                        aula.getModalidade(),
                        aula.getDuracaoAula(),
                        aula.getAluno().getId(),
                        aula.getDataHoraAula()
                ));
    }

    @Transactional
    public AulaResponse atualizarAula(UUID aulaId, AulaRequest dadosAtualizados, User usuarioLogado) {

        Funcionario professor = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RuntimeException("Aula inexistente"));
        Aluno aluno = alunoRepository.findById(dadosAtualizados.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno inexistente"));

        aula.setAluno(aluno);
        aula.setProfessor(professor);
        AtualizarDadosAula(aula, dadosAtualizados);

        aulaRepository.save(aula);
        return toResponse(aula);
    }

    @Transactional
    public void cancelarAula(UUID aulaId) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RuntimeException("Aula inexistente"));

        aula.setStatusAula(StatusAula.CANCELADA);
        aulaRepository.save(aula);
    }
}
