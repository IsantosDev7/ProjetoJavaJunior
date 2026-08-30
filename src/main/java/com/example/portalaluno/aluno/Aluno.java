package com.example.portalaluno.aluno;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.responsavel.Responsavel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status_matricula",nullable = false)
    private AlunoStatusMatricula alunoStatusMatricula = AlunoStatusMatricula.ATIVO;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AlunoStatusCadastro status = AlunoStatusCadastro.PENDENTE;

    // metodo para verificação de idade
    public boolean isMinor() {
        return birthDate.plusYears(18).isAfter(LocalDate.now());
    }
}
