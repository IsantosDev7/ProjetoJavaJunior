package com.example.portalaluno.aula.dto;


import com.example.portalaluno.aula.roles.Modalidade;

import java.time.OffsetDateTime;
import java.util.UUID;

public class AulaRequest {

    private String titulo;
    private Modalidade modalidade;
    private Integer duracao;
    private UUID alunoId;
    private OffsetDateTime dataHoraAula;

    public String getTitulo() {return titulo;}
    public void setTitulo(String titulo) {this.titulo = titulo;}

    public Modalidade getModalidade() {return modalidade;}
    public void setModalidade(Modalidade modalidade){this.modalidade = modalidade;}

    public Integer getDuracao() {return duracao;}
    public void setDuracao(Integer duracao) {this.duracao = duracao;}

    public UUID getAlunoId() {return alunoId;}
    public void setAlunoId(UUID alunoId) {this.alunoId = alunoId;}

    public OffsetDateTime getDataHoraAula() {return dataHoraAula;}
    public void setDataHoraAula(OffsetDateTime dataHoraAula){this.dataHoraAula = dataHoraAula;}
}
