package com.example.portalaluno.relatorio;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.funcionario.FuncionarioSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("relatorioSecurity")
public class RelatorioSecurity {

    private final RelatorioRepository relatorioRepository;
    private final FuncionarioSecurity funcionarioSecurity;

    public RelatorioSecurity(RelatorioRepository relatorioRepository, FuncionarioSecurity funcionarioSecurity) {
        this.relatorioRepository = relatorioRepository;
        this.funcionarioSecurity = funcionarioSecurity;
    }
    public boolean podeEditar(Authentication authentication, UUID relatorioId){
        User usuarioLogado = (User) authentication.getPrincipal();

        if (usuarioLogado.getRole() == UserRole.SUPER_ADMIN) {
            return true;
        }

        // Verifica se é Coordenador. Se não for funcionário, RuntimeException é lançada
        try {
            if (funcionarioSecurity.temCargo(authentication, "Coordenador")) {
                return true;
            }
        } catch (RuntimeException e) {
            // Usuário não é funcionário, isso é ok — segue para validar se é dono
        }

        Relatorio relatorio = relatorioRepository.findById(relatorioId).orElse(null);
        if (relatorio != null && relatorio.getProfessor().getUsuario().getId().equals(usuarioLogado.getId())) {
            return true;
        }
        return false;
    }
}
