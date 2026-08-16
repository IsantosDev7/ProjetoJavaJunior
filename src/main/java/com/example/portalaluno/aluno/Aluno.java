package com.example.portalaluno.aluno;

import com.example.portalaluno.responsavel.Responsavel;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "aluno")
public class Aluno {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = true)
    private Responsavel responsavel;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "cpf", nullable = true, unique = true, length = 11)
    private String cpf;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "phone", nullable = false, length = 50)
    private String phone;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthDate;

    @Column(name ="address", nullable = false, length = 255)
    private String address;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // métodos get:

    public UUID getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCpf() {
        return cpf;
    }
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phone;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public String getAddress() {
        return address;
    }
    public String getCep() {
        return cep;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public Responsavel getResponsavel() {
        return responsavel;
    }
    public String getCountry() {
        return country;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // métodos set:

    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setState(String state) {
        this.state = state;
    }
    public void setResponsavel(Responsavel responsavel) {
        this.responsavel = responsavel;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Aluno() {
    }

    public Aluno(Responsavel responsavel, String name, String cpf, String password, String email, String phone, LocalDate birthDate, String address, String cep, String city, String state, String country) {
        this.responsavel = responsavel;
        this.name = name;
        this.cpf = cpf;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.address = address;
        this.cep = cep;
        this.city = city;
        this.state = state;
        this.country = country;
    }
}
