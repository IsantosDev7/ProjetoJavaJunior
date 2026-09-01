package com.example.portalaluno.aluno.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoRequest {

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
    private String cpf;

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
// dto criada para evitar uso direto da entidade aluno em posts direto do controller