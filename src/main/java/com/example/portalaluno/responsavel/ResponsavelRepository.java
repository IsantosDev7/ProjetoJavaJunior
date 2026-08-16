package com.example.portalaluno.responsavel;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsavelRepository extends JpaRepository<Responsavel, UUID> {

    List<Responsavel> findByNameContainingIgnoreCase(String name);
    Optional<Responsavel> findByCpf(String cpf);

}
