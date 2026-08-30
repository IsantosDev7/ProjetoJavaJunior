package com.example.portalaluno.funcionario;

import com.example.portalaluno.auth.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioSecurity {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioSecurity(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public boolean temCargo(Authentication authentication, String nomeCargo) {
        User usuarioLogado = (User) authentication.getPrincipal();

        Funcionario funcionario = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado."));

        return funcionario.getCargos().stream()
                .anyMatch(cargo -> cargo.getName().equals(nomeCargo));
    }
}