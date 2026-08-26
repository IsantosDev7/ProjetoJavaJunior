package com.example.portalaluno.pagamento;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", unique = true, nullable = false)
    private UUID id;

    @Column(name = "valor", nullable = false, length = 255)
    private BigDecimal value;

    @Column(name = "data",  nullable = false)
    private OffsetDateTime data;

    public Pagamento() {}

    public Pagamento(UUID id, BigDecimal value, OffsetDateTime data) {
        this.id = id;
        this.value = value;
        this.data = data;
    }

    // métodos get
    public UUID getId() {return id;}
    public BigDecimal getValue() {return value;}
    public OffsetDateTime getData() {return data;}

    // métodos set
    public void setId(UUID id) {this.id = id;}
    public void setValue(BigDecimal value) {this.value = value;}
    public void setData(OffsetDateTime data) {this.data = data;}
}
