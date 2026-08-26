package com.example.portalaluno.funcionario;

import com.example.portalaluno.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {

    List<Funcionario> findByNameContainingIgnoreCase(String name);
    Optional<Funcionario> findByUsuario(User usuario);

}
