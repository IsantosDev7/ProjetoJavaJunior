package com.example.portalaluno.suporte;

import com.example.portalaluno.aluno.Aluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "suporte")
public class Chamado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "titulo")
    @NotBlank
    @Size(min = 10, max = 100)
    private String titulo;

    @Column(name = "descricao")
    @NotBlank
    @Size(min = 10, max = 500)
    private String descricao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno alunoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridade")
    private ChamadoPrioridade prioridade = ChamadoPrioridade.LOW;

    @Column(name = "status_chamado")
    private boolean resolvido = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
