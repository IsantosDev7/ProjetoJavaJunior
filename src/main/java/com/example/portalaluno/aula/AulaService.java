package com.example.portalaluno.aula;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.aluno.dto.AlunoRequest;
import com.example.portalaluno.aula.dto.AulaRequest;
import com.example.portalaluno.aula.roles.StatusAula;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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
}
