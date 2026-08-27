package com.example.portalaluno.funcionario.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FuncionarioRequest {

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
