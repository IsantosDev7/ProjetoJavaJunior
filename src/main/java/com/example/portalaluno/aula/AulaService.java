package com.example.portalaluno.aula;

import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.aluno.dto.AlunoRequest;
import com.example.portalaluno.aula.dto.AulaRequest;
import com.example.portalaluno.aula.roles.Modalidade;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AulaService {

    private AlunoRequest alunoRequest;
    private AlunoRepository alunoRepository;
    private FuncionarioRepository funcionarioRepository;

    public AulaService(AlunoRepository alunoRepository, FuncionarioRepository funcionarioRepository) {
        this.alunoRepository = alunoRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public Aula cadastrarAula(AulaRequest dadosAula, AlunoRequest dadosAlunoRequest, AlunoRepository alunoRepository) {
        Aula novaAula = new Aula();
        novaAula.setTitulo(dadosAula.getTitulo());
        novaAula.setModalidade(dadosAula.getModalidade());
        novaAula.setDuracaoAula(dadosAula.getDuracao());
        novaAula.setDataHoraAula(dadosAula.getDataHoraAula());
    }

}
