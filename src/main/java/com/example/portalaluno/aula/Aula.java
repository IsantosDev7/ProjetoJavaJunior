package com.example.portalaluno.aula;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aula.roles.Modalidade;
import com.example.portalaluno.aula.roles.StatusAula;
import com.example.portalaluno.funcionario.Funcionario;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

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

    public Aula() {}

    public Aula(UUID id, Funcionario professor, Aluno aluno, Integer duracaoAula, String titulo,  OffsetDateTime dataHoraAula, Modalidade modalidade, StatusAula statusAula) {
        this.id = id;
        this.professor = professor;
        this.aluno = aluno;
        this.duracaoAula = duracaoAula;
        this.titulo = titulo;
        this.dataHoraAula = dataHoraAula;
        this.modalidade = modalidade;
        this.statusAula = statusAula;
    }

    // métodos get:
    public UUID getId() {return id;}
    public Funcionario getProfessor() {return professor;}
    public Aluno getAluno() {return aluno;}
    public Integer getDuracaoAula() {return duracaoAula;}
    public String getTitulo() {return titulo;}
    public OffsetDateTime getDataHoraAula() {return dataHoraAula;}
    public Modalidade getModalidade() {return modalidade;}
    public StatusAula getStatusAula() {return statusAula;}

    // métodos set:
    public void setId(UUID id) {this.id = id;}
    public void setProfessor(Funcionario professor) {this.professor = professor;}
    public void setAluno(Aluno aluno) {this.aluno = aluno;}
    public void setDuracaoAula(Integer duracaoAula) {this.duracaoAula = duracaoAula;}
    public void setTitulo(String titulo) {this.titulo = titulo;}
    public void setDataHoraAula(OffsetDateTime dataHoraAula) {this.dataHoraAula = dataHoraAula;}
    public void setModalidade(Modalidade modalidade) {this.modalidade = modalidade;}
    public void setStatusAula(StatusAula statusAula) {this.statusAula = statusAula;}

}
