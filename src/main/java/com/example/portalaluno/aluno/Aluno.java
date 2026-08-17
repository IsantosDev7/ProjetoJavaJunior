package com.example.portalaluno.aluno;

import com.example.portalaluno.auth.User;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = true)
    private Responsavel responsavel;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "cpf", nullable = true, unique = true, length = 11)
    private String cpf;

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

    public Aluno() {
    }

    public Aluno(Responsavel responsavel, User usuario, String name, String cpf, String password, String email, String phone, LocalDate birthDate, String address, String cep, String city, String state, String country) {
        this.responsavel = responsavel;
        this.usuario = usuario;
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.birthDate = birthDate;
        this.address = address;
        this.cep = cep;
        this.city = city;
        this.state = state;
        this.country = country;
    }

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
    public User getUsuario() {return usuario; }
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
    public void setUsuario(User usuario) {this.usuario = usuario; }

    // méttodo para verificação de idade
    public boolean isMinor() {
        return birthDate.plusYears(18).isAfter(LocalDate.now());
    }
}
