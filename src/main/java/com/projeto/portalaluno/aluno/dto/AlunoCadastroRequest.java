package com.projeto.portalaluno.aluno.dto;

import com.projeto.portalaluno.responsavel.dto.ResponsavelRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlunoCadastroRequest {

    @Valid
    private AlunoRequest aluno;

    @Valid
    private ResponsavelRequest responsavel;
}
//dto criada para facilitar o cadastro do aluno