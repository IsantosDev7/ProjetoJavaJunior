package com.example.portalaluno.relatorio;

import com.example.portalaluno.aula.Aula;
import com.example.portalaluno.funcionario.Funcionario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "aula_id",  nullable = false)
    private Aula aula;

    @ManyToOne
    @JoinColumn(name = "professor_id",  nullable = false)
    private Funcionario professor;

    @NotBlank
    @Column(name = "texto")
    private String texto;

    @Column(name = "status")
    private Boolean lido = false;
}
