package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoResponse;
import com.example.portalaluno.aluno.dto.AlunoCadastroRequest;
import com.example.portalaluno.auth.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;


@RestController
@RequestMapping("/aluno")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @PostMapping
    public ResponseEntity<AlunoResponse> cadastrar(@Valid @RequestBody AlunoCadastroRequest request) {
        AlunoResponse alunoSalvo = alunoService.cadastrar(request.getAluno(), request.getResponsavel());
        return ResponseEntity.status(HttpStatus.CREATED).body(alunoSalvo);
    }

    @PutMapping("/perfil")
    public ResponseEntity<AlunoResponse> atualizarMeuCadastro(@Valid @RequestBody AlunoCadastroRequest request, @AuthenticationPrincipal User usuarioLogado) {
        AlunoResponse alunoAtualizado = alunoService.atualizarMeuCadastro(request.getAluno(), usuarioLogado);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Secretário') or @funcionarioSecurity.temCargo(authentication, 'Coordenador') or @funcionarioSecurity.temCargo(authentication, 'Administrador')")
    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponse> atualizarCadastroAluno(@PathVariable UUID id, @Valid @RequestBody AlunoCadastroRequest request) {
        AlunoResponse alunoAtualizado = alunoService.atualizarCadastroAluno(id, request.getAluno());
        return ResponseEntity.ok(alunoAtualizado);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or @funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<AlunoResponse>> consultarAlunos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String name) {

        return ResponseEntity.ok(alunoService.consultarAlunos(name, pagina, tamanho));
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or @funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> aprovarCadastroAluno(@PathVariable UUID id) {
        alunoService.aprovarAluno(id);
        return ResponseEntity.ok("Cadastro do aluno aprovado com sucesso!");
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<String> cancelarMatriculaAluno(@PathVariable UUID id) {
        alunoService.cancelarMatriculaAluno(id);
        return ResponseEntity.ok("Matrícula do aluno cancelada com sucesso!");
    }
}
