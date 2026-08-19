package com.example.portalaluno.cargo;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "cargo")
public class Cargo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    public Cargo() {
    }
    public Cargo(String name) {
        this.name = name;
    }

    public UUID getId() {return id;}
    public String getName() {return name;}
    public void setId(String name) {this.name = name;}
}
