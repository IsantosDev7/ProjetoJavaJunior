package com.example.portalaluno.funcionario.dto;

import java.time.LocalDate;
import java.util.UUID;

public class FuncionarioResponse {

    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private String phone;
    private LocalDate birthDate;

    public FuncionarioResponse(UUID id, String name, String email, String cpf, String phone, LocalDate birthDate) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.phone = phone;
        this.birthDate = birthDate;
    }

    //métodos getter:
    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getCpf() {return cpf;}
    public String getPhone() {return phone;}
    public LocalDate getBirthDate() {return birthDate;}

    //métodos setter:
    public void setId(UUID id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}
    public void setCpf(String cpf) {this.cpf = cpf;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}
}
