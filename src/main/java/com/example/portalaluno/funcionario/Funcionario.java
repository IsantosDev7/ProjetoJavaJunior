package com.example.portalaluno.funcionario;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.cargo.Cargo;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "funcionario")
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToMany
    @JoinTable(
            name = "funcionario_cargo",
            joinColumns = @JoinColumn(name = "funcionario_id"),
            inverseJoinColumns = @JoinColumn(name = "cargo_id")
    )
    private Set<Cargo> cargos;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthDate;

    @Column(name = "cpf", nullable = true, unique = true, length = 11)
    private String cpf;

    @Column(name = "phone", nullable = false, length = 50)
    private String phone;

    @Column(name ="address", nullable = false, length = 255)
    private String address;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "cep", nullable = false, length = 8)
    private String cep;

    @Column(name = "country", nullable = false, length = 50)
    private String country;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public Funcionario(){
    }

    public Funcionario(Set<Cargo> cargos, LocalDateTime createdAt, LocalDate birthDate, User usuario, String name, String cpf, String phone, String address, String state, String cep, String country, String city) {
        this.usuario = usuario;
        this.name = name;
        this.cpf = cpf;
        this.phone = phone;
        this.address = address;
        this.state = state;
        this.cep = cep;
        this.country = country;
        this.createdAt = createdAt;
        this.birthDate = birthDate;
        this.city = city;
        this.cargos =  cargos;
    }
    // métodos get
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
    public String getCountry() {
        return country;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public User getUsuario() {return usuario; }
    public Set<Cargo> getCargos() {return cargos;}

    //métodos set
    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCpf(String cpf) {this.cpf = cpf; }
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
    public void setCountry(String country) {
        this.country = country;
    }
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt; }
    public void setUsuario(User usuario) {this.usuario = usuario; }
    public void setCargos(Set<Cargo> cargos) {this.cargos = cargos;}
}
