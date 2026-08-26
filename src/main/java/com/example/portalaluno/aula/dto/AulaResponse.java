package com.example.portalaluno.aula.dto;

import com.example.portalaluno.aula.roles.Modalidade;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AulaResponse {

    private UUID id;
    private String titulo;
    private Modalidade modalidade;
    private Integer duracao;
    private UUID alunoId;
    private OffsetDateTime dataHoraAula;

    public AulaResponse(UUID id, String titulo, Modalidade modalidade, Integer duracao, UUID alunoId, OffsetDateTime dataHoraAula) {
        this.id = id;
        this.titulo = titulo;
        this.modalidade = modalidade;
        this.duracao = duracao;
        this.alunoId = alunoId;
        this.dataHoraAula = dataHoraAula;
    }

    // métodos get:

    public String getTitulo() {return titulo;}
    public Modalidade getModalidade() {return modalidade;}
    public Integer getDuracao() {return duracao;}
    public UUID getAlunoId() {return alunoId;}
    public OffsetDateTime getDataHoraAula() {return dataHoraAula;}
    public UUID getId() {return id;}

}
