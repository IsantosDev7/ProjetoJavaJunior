package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoResponse;
import com.example.portalaluno.aluno.dto.CadastroAlunoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @PostMapping
    public AlunoResponse cadastrar(@RequestBody CadastroAlunoRequest request) {
        Aluno alunoSalvo = alunoService.cadastrar(request.getAluno(), request.getResponsavel());

        return new AlunoResponse(
                alunoSalvo.getId(),
                alunoSalvo.getName(),
                alunoSalvo.getUsuario().getEmail(),
                alunoSalvo.getCpf(),
                alunoSalvo.getPhone(),
                alunoSalvo.getBirthDate()
        );
    }
    @PreAuthorize("hasRole('FUNCIONARIO')")
    @GetMapping
    public List<Aluno> consultarAlunosPorNome(@RequestParam String name) {
        return alunoService.consultarAlunosPorNome(name);
    }
}
