package com.example.portalaluno.aluno.dto;

import java.time.LocalDate;

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

    public String getName() {return name; }
    public void setName(String name) {this.name = name; }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {this.city = city; }

    public String getState() {return state; }
    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
}
