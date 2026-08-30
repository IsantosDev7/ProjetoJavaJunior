package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoResponse;
import com.example.portalaluno.aluno.dto.CadastroAlunoRequest;
import com.example.portalaluno.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


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
                alunoSalvo.getBirthDate(),
                null
        );
    }
    @PutMapping("/perfil")
    public AlunoResponse atualizarMeuCadastro(@RequestBody CadastroAlunoRequest request){
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Aluno alunoAtualizado = alunoService.atualizarMeuCadastro(request.getAluno(), usuarioLogado);

        return new AlunoResponse(
                alunoAtualizado.getId(),
                alunoAtualizado.getName(),
                alunoAtualizado.getUsuario().getEmail(),
                alunoAtualizado.getCpf(),
                alunoAtualizado.getPhone(),
                alunoAtualizado.getBirthDate(),
                null
        );
    }

    @PutMapping("/{id}")
    public AlunoResponse atualizarCadastroAluno(@PathVariable UUID id, @RequestBody CadastroAlunoRequest request){
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Aluno alunoAtualizado = alunoService.atualizarCadastroAluno(id, request.getAluno(), usuarioLogado);

        return new AlunoResponse(
                alunoAtualizado.getId(),
                alunoAtualizado.getName(),
                alunoAtualizado.getCpf(),
                alunoAtualizado.getUsuario().getEmail(),
                alunoAtualizado.getPhone(),
                alunoAtualizado.getBirthDate(),
                null
        );
    }

    @PreAuthorize("hasRole('FUNCIONARIO')")
    @GetMapping
    public List<AlunoResponse> consultarAlunosPorNome(@RequestParam String name) {
        return alunoService.consultarAlunosPorNome(name);
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SECRETARIO', 'COORDENADOR')")
    public ResponseEntity<String> aprovarCadastroAluno(@PathVariable UUID id) {
        alunoService.aprovarAluno(id);
        return ResponseEntity.ok("Cadastro do aluno aprovado com sucesso!");
    }
}
