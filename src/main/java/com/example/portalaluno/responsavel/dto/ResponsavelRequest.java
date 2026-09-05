package com.example.portalaluno.responsavel.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class ResponsavelRequest {

    @NotBlank
    @Size(min = 2, max = 255)
    @Pattern(regexp = "^[A-Za-zÀ-ú]+\\s[A-Za-zÀ-ú\\s]+$")
    private String name;

    @NotBlank
    @CPF
    private String cpf;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\(\\d{2}\\) \\d{5}-\\d{4}$")
    private String phone;

    @NotNull
    @Past
    private LocalDate birthdate;
}
