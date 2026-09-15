package com.projeto.portalaluno.funcionario;

import com.projeto.portalaluno.auth.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {

    Page<Funcionario> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Optional<Funcionario> findByUsuario(User usuario);

}
