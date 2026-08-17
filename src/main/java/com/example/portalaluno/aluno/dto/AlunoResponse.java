package com.example.portalaluno.aluno.dto;

import java.time.LocalDate;
import java.util.UUID;

public class AlunoResponse {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private LocalDate birthDate;

    public AlunoResponse(UUID id, String name, String email, String cpf, String phone, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    // getters

    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {return email; }
    public String getCpf() {
        return cpf;
    }
    public String getPhone() {
        return phone;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
}