package com.example.portalaluno.funcionario;


import com.example.portalaluno.funcionario.dto.CadastroFuncionarioRequest;
import com.example.portalaluno.funcionario.dto.FuncionarioResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping
    public List<Funcionario> consultarFuncionariosPorNome(@RequestParam String name){
        return funcionarioService.consultarFuncionariosPorNome(name);
    }

}
