package com.example.portalaluno.aula;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.aula.dto.AulaRequest;
import com.example.portalaluno.aula.roles.StatusAula;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;
    private AlunoRepository alunoRepository;
    private FuncionarioRepository funcionarioRepository;

    public AulaService(AlunoRepository alunoRepository, FuncionarioRepository funcionarioRepository, AulaRepository aulaRepository) {
        this.alunoRepository = alunoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.aulaRepository = aulaRepository;
    }

    @Transactional
    public Aula cadastrarAula(AulaRequest dadosAula, User usuarioLogado) {

        Funcionario professor = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));

        Aluno aluno = alunoRepository.findById(dadosAula.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        Aula novaAula = new Aula();
        novaAula.setTitulo(dadosAula.getTitulo());
        novaAula.setModalidade(dadosAula.getModalidade());
        novaAula.setDuracaoAula(dadosAula.getDuracao());
        novaAula.setDataHoraAula(dadosAula.getDataHoraAula());
        novaAula.setAluno(aluno);
        novaAula.setStatusAula(StatusAula.PREVISTA);
        novaAula.setProfessor(professor);

        return aulaRepository.save(novaAula);
    }


    public Aula atualizarAula(UUID aulaId, AulaRequest dadosAtualizados, User usuarioLogado) {

        Funcionario professor = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RuntimeException("Aula inexistente"));
        Aluno aluno = alunoRepository.findById(dadosAtualizados.getAlunoId())
                .orElseThrow(() -> new RuntimeException("Aluno inexistente"));

        aula.setTitulo(dadosAtualizados.getTitulo());
        aula.setModalidade(dadosAtualizados.getModalidade());
        aula.setDuracaoAula(dadosAtualizados.getDuracao());
        aula.setDataHoraAula(dadosAtualizados.getDataHoraAula());
        aula.setAluno(aluno);
        aula.setStatusAula(StatusAula.PREVISTA);
        aula.setProfessor(professor);

        return aulaRepository.save(aula);
    }

    public Aula cancelarAula(UUID aulaId) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RuntimeException("Aula inexistente"));

        aula.setStatusAula(StatusAula.CANCELADA);
        return aulaRepository.save(aula);
    }
}
