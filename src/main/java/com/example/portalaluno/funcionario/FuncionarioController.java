package com.example.portalaluno.funcionario;


import com.example.portalaluno.funcionario.dto.CadastroFuncionarioRequest;
import com.example.portalaluno.funcionario.dto.FuncionarioRequest;
import com.example.portalaluno.funcionario.dto.FuncionarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/funcionario")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @PostMapping
    public FuncionarioResponse cadastrarFuncionario(@RequestBody CadastroFuncionarioRequest request){
        Funcionario funcionarioSalvo = funcionarioService.cadastrarFuncionario(
                request.getFuncionario(),
                request.getCargos());

        return new FuncionarioResponse(
                funcionarioSalvo.getId(),
                funcionarioSalvo.getName(),
                funcionarioSalvo.getUsuario().getEmail(),
                funcionarioSalvo.getCpf(),
                funcionarioSalvo.getPhone(),
                funcionarioSalvo.getBirthDate()
        );

    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> desativarFuncionario(@PathVariable UUID id){
        funcionarioService.desativarFuncionario(id);
        return ResponseEntity.ok("Funcionário desativado com sucesso");
    }

    @GetMapping
    public List<Funcionario> consultarFuncionariosPorNome(@RequestParam String name){
        return funcionarioService.consultarFuncionariosPorNome(name);
    }



}
