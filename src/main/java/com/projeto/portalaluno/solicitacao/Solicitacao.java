package com.projeto.portalaluno.solicitacao;

import com.projeto.portalaluno.funcionario.Funcionario;
import com.projeto.portalaluno.solicitacao.status.SolicitacaoStatus;
import com.projeto.portalaluno.solicitacao.status.SolicitacaoTipo;
import jakarta.persistence.*;
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
@Table(name = "solicitacao")
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private SolicitacaoTipo tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    @Column(name = "funcionario_alvo_id")
    private UUID funcionarioAlvoId;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SolicitacaoStatus status = SolicitacaoStatus.PENDENTE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
