package com.example.portalaluno.funcionario;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {

    List<Funcionario> findByNameContainingIgnoreCase(String name);

    String name(String name);
}
