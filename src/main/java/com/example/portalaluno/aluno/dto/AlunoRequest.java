package com.example.portalaluno.aluno.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoRequest {
    private String name;
    private String email;
    private String password;
    private String cpf;
    private String phone;
    private LocalDate birthDate;
    private String address;
    private String cep;
    private String city;
    private String state;
    private String country;
}
