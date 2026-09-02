package com.example.portalaluno.responsavel;

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

    public ResponsavelService(ResponsavelRepository responsavelRepository, FuncionarioRepository funcionarioRepository) {
        this.responsavelRepository = responsavelRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    public List<Responsavel> consultarReponsavelPorNome (String name){
        List<Responsavel> responsaveis = responsavelRepository.findByNameContainingIgnoreCase(name);

        if (responsaveis.isEmpty()) {
                throw new RuntimeException("Nenhum aluno encontrado com esse nome.");
        }
        return responsaveis;
    }

    public Responsavel atualizarResponsavel(ResponsavelRequest dadosAtualizados, User usuarioLogado, UUID responsavelId) {
        Funcionario funcionario = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));
        Responsavel responsavel = responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new RuntimeException("Responsavel inexistente com esse id"));

        Responsavel novoResponsavel = new Responsavel();
        novoResponsavel.setName(dadosAtualizados.getName());
        novoResponsavel.setCpf(dadosAtualizados.getCpf());
        novoResponsavel.setEmail(dadosAtualizados.getEmail());
        novoResponsavel.setPhone(dadosAtualizados.getPhone());
        novoResponsavel.setBirthdate(dadosAtualizados.getBirthdate());

        return  responsavelRepository.save(novoResponsavel);
    }
}
