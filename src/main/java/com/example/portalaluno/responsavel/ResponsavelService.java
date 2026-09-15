package com.example.portalaluno.responsavel;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import com.example.portalaluno.responsavel.dto.ResponsavelResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;


@Service
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;
    private final AlunoRepository alunoRepository;

    public ResponsavelService(ResponsavelRepository responsavelRepository, AlunoRepository alunoRepository) {
        this.responsavelRepository = responsavelRepository;
        this.alunoRepository = alunoRepository;
    }

    private ResponsavelResponse toResponse(Responsavel responsavel) {
        return new ResponsavelResponse(
                responsavel.getName(),
                responsavel.getEmail(),
                responsavel.getPhone()
        );
    }

    @Transactional(readOnly = true)
    public Page<ResponsavelResponse> listarResponsavel(String name, int pagina, int tamanho){
        Sort.Order order = Sort.Order.asc("name").ignoreCase();
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(order));

        Page<Responsavel> page;
        if (name != null && !name.isBlank()) {
            page = responsavelRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = responsavelRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }


    @Transactional
    public ResponsavelResponse atualizarResponsavel(ResponsavelRequest dadosAtualizados, User usuarioLogado, UUID responsavelId) {
        Responsavel responsavel = responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new RuntimeException("Responsável inexistente com esse id"));


        responsavel.setName(dadosAtualizados.getName());
        responsavel.setCpf(dadosAtualizados.getCpf());
        responsavel.setEmail(dadosAtualizados.getEmail());
        responsavel.setPhone(dadosAtualizados.getPhone());
        responsavel.setBirthdate(dadosAtualizados.getBirthdate());

        Responsavel responsavelAtualizado = responsavelRepository.save(responsavel);
        return  toResponse(responsavelAtualizado);
    }

    @Transactional
    public ResponsavelResponse atualizarMeuResponsavel(ResponsavelRequest dadosAtualizados, User usuarioLogado, UUID responsavelId) {
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("\"Aluno não encontrado para este usuário\""));
        Responsavel responsavel = responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new RuntimeException("Responsável inexistente com esse id"));

        if (aluno.getResponsavel() == null || !aluno.getResponsavel().equals(responsavel)) {
            throw new RuntimeException("Esse responsável não pertence ao aluno logado ou aluno logado não possui responsável cadastrado");
        } else {
            responsavel.setName(dadosAtualizados.getName());
            responsavel.setCpf(dadosAtualizados.getCpf());
            responsavel.setEmail(dadosAtualizados.getEmail());
            responsavel.setPhone(dadosAtualizados.getPhone());
            responsavel.setBirthdate(dadosAtualizados.getBirthdate());
        }

        Responsavel responsavelAtualizado = responsavelRepository.save(responsavel);
        return toResponse(responsavelAtualizado);
    }

    @Transactional
    public void desativarResponsavel(UUID responsavelId) {
        Responsavel responsavel = responsavelRepository.findById(responsavelId)
                .orElseThrow(() -> new RuntimeException("Responsável não encontrando"));
        if (responsavel.getStatus().equals(StatusResponsavel.CANCELADO)){
            throw new RuntimeException("Responsável já se encontra cancelado");
        }
        responsavel.setStatus(StatusResponsavel.CANCELADO);
        responsavelRepository.save(responsavel);
    }

}
