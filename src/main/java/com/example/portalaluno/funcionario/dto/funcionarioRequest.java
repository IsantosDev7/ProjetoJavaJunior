package com.example.portalaluno.funcionario.dto;

import java.time.LocalDate;

public class funcionarioRequest {

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

    //métodos get
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getCpf() {return cpf;}
    public String getPhone() {return phone;}
    public LocalDate getBirthDate() {return birthDate;}
    public String getAddress() {return address;}
    public String getCep() {return cep;}
    public String getCity() {return city;}
    public String getState() {return state;}
    public String getCountry() {return country;}

    //métodos set
    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
    public void setCpf(String cpf) {this.cpf = cpf;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setBirthDate(LocalDate birthDate) {this.birthDate = birthDate;}
    public void  setAddress(String address) {this.address = address;}
    public void setCep(String cep) {this.cep = cep;}
    public void setCity(String city) {this.city = city;}
    public void setState(String state) {this.state = state;}
    public void setCountry(String country) {this.country = country;}
}
