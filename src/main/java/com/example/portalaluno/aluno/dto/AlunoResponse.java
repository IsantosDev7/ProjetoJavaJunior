package com.example.portalaluno.aluno.dto;

import com.example.portalaluno.responsavel.dto.ResponsavelResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlunoResponse {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private LocalDate birthDate;
    private ResponsavelResponse responsavelResponse;
}