package com.example.portalaluno.aula;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aula.roles.Modalidade;
import com.example.portalaluno.aula.roles.StatusAula;
import com.example.portalaluno.funcionario.Funcionario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "aula")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "professor_id",  nullable = false)
    private Funcionario professor;

    @ManyToOne
    @JoinColumn(name = "aluno_id",   nullable = false)
    private Aluno aluno;

    @Column(name = "duracao_aula",  nullable = false)
    private Integer duracaoAula;

    @Column(name = "titulo",  nullable = false, length = 255)
    private String titulo;

    @Column(name = "data_hora_aula",   nullable = false)
    private OffsetDateTime dataHoraAula;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidade", nullable = false)
    private Modalidade modalidade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aula")
    private StatusAula statusAula;

}
