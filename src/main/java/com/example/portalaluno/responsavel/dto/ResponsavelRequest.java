package com.example.portalaluno.responsavel.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResponsavelRequest {
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private LocalDate birthdate;
}
