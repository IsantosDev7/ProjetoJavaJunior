package com.projeto.portalaluno.responsavel;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponsavelRepository extends JpaRepository<Responsavel, UUID> {

    Page<Responsavel> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Responsavel> findByCpf(String cpf);
    Optional<Responsavel> findById(UUID id);

}
