package com.example.portalaluno.relatorio;

import com.example.portalaluno.aula.Aula;
import com.example.portalaluno.funcionario.Funcionario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "relatorio")
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

    @Column(name = "lido")
    private Boolean lido = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusRelatorio status = StatusRelatorio.ATIVO;
}
