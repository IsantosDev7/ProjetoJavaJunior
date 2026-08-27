package com.example.portalaluno.funcionario.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FuncionarioResponse {

    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private LocalDate birthDate;

}
