package com.example.portalaluno.funcionario.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
public class FuncionarioRequest {

    @NotBlank
    @Size(min = 2, max = 255)
    @Pattern(regexp = "^[A-Za-zÀ-ú]+\\s[A-Za-zÀ-ú\\s]+$")
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 255)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$")
    private String password;

    @CPF
    @NotBlank
    private String cpf;

    @NotBlank
    @Pattern(regexp = "^\\(\\d{2}\\) \\d{5}-\\d{4}$")
    private String phone;

    @Past
    @NotNull
    private LocalDate birthDate;

    @NotBlank
    private String address;

    @NotNull
    @Pattern(regexp = "^\\d{5}-\\d{3}$")
    private String cep;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;
}
// dto criada para evitar uso direto da entidade funcionario em posts direto do controller