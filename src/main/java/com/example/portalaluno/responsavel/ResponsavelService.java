package com.example.portalaluno.responsavel;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;


@Service
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final AlunoRepository alunoRepository;

    public ResponsavelService(ResponsavelRepository responsavelRepository, FuncionarioRepository funcionarioRepository, AlunoRepository alunoRepository) {
        this.responsavelRepository = responsavelRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.alunoRepository = alunoRepository;
    }

    public List<Responsavel> consultarReponsavelPorNome (String name){
        List<Responsavel> responsaveis = responsavelRepository.findByNameContainingIgnoreCase(name);

        if (responsaveis.isEmpty()) {
                throw new RuntimeException("Nenhum aluno encontrado com esse nome.");
        }
        return responsaveis;
    }
    // rota para o funcionário atualizar responsável
    public Responsavel atualizarResponsavel(ResponsavelRequest dadosAtualizados, User usuarioLogado, UUID responsavelId) {
        Funcionario funcionario = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));
        Responsavel responsavel = responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new RuntimeException("Responsável inexistente com esse id"));


        responsavel.setName(dadosAtualizados.getName());
        responsavel.setCpf(dadosAtualizados.getCpf());
        responsavel.setEmail(dadosAtualizados.getEmail());
        responsavel.setPhone(dadosAtualizados.getPhone());
        responsavel.setBirthdate(dadosAtualizados.getBirthdate());

        return  responsavelRepository.save(responsavel);
    }

    public Responsavel atualizarMeuResponsavel(ResponsavelRequest dadosAtualizados, User usuarioLogado, UUID responsavelId) {
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("\"Aluno não encontrado para este usuário\""));
        Responsavel responsavel = responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new RuntimeException("Responsavel inexistente com esse id"));

        if (aluno.getResponsavel() == null || !aluno.getResponsavel().equals(responsavel)) {
            throw new RuntimeException("Esse responsável não pertence ao aluno logado ou aluno logado não possui responsável cadastrado");
        } else {
            responsavel.setName(dadosAtualizados.getName());
            responsavel.setCpf(dadosAtualizados.getCpf());
            responsavel.setEmail(dadosAtualizados.getEmail());
            responsavel.setPhone(dadosAtualizados.getPhone());
            responsavel.setBirthdate(dadosAtualizados.getBirthdate());
        }
        return responsavelRepository.save(responsavel);
    }
}
