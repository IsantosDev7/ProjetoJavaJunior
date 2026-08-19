package com.example.portalaluno.responsavel;

import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ResponsavelService {

    private final ResponsavelRepository responsavelRepository;

    public ResponsavelService(ResponsavelRepository responsavelRepository) {
        this.responsavelRepository = responsavelRepository;
    }

    public List<Responsavel> consultarReponsavelPorNome (String name){
        List<Responsavel> responsaveis = responsavelRepository.findByNameContainingIgnoreCase(name);

        if (responsaveis.isEmpty()) {
                throw new RuntimeException("Nenhum aluno encontrado com esse nome.");
        }
        return responsaveis;
    }
}
