package com.projeto.portalaluno.funcionario;


import com.projeto.portalaluno.funcionario.dto.CadastroFuncionarioRequest;
import com.projeto.portalaluno.funcionario.dto.FuncionarioResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @PostMapping
    public ResponseEntity<FuncionarioResponse> cadastrarFuncionario(@Valid @RequestBody CadastroFuncionarioRequest request){
        FuncionarioResponse funcionarioSalvo = funcionarioService.cadastrarFuncionario(
                request.getFuncionario(),
                request.getCargos());
        return ResponseEntity.ok(funcionarioSalvo);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> desativarFuncionario(@PathVariable UUID id){
        funcionarioService.desativarFuncionario(id);
        return ResponseEntity.ok("Funcionário desativado com sucesso");
    }

    @PreAuthorize("hasRole('SUPER_ADMIN') or @funcionarioSecurity.temCargo(authentication, 'Coordenador')")
    @GetMapping
    public ResponseEntity<Page<FuncionarioResponse>> listarFuncionarios(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho){
        Page<FuncionarioResponse> chamado = funcionarioService.listarFuncionarios(name, pagina, tamanho);
        return ResponseEntity.ok(chamado);
    }
}
