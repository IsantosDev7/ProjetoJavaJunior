package com.example.portalaluno.aluno.dto;

import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastroAlunoRequest {
    private AlunoRequest aluno;
    private ResponsavelRequest responsavel;
}