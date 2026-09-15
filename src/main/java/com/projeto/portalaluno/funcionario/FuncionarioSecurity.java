package com.projeto.portalaluno.funcionario;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.auth.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class FuncionarioSecurity {

    private final FuncionarioRepository funcionarioRepository;

    public FuncionarioSecurity(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
    }

    public boolean temCargo(Authentication authentication, String nomeCargo) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User usuarioLogado)) {
            return false;
        }

        // Admin não precisa ter registro de Funcionario nem Cargo vinculado
        if (usuarioLogado.getRole() == UserRole.SUPER_ADMIN) {
            return true;
        }

        // Retorna false em vez de estourar exceção caso o usuário não seja um Funcionário
        return funcionarioRepository.findByUsuario(usuarioLogado)
                .map(funcionario -> funcionario.getCargos().stream()
                        .anyMatch(cargo -> cargo.getName().equalsIgnoreCase(nomeCargo)))
                .orElse(false);
    }
}