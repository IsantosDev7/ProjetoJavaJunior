package com.example.portalaluno.aula;

import com.example.portalaluno.aula.dto.AulaRequest;
import com.example.portalaluno.aula.dto.AulaResponse;
import com.example.portalaluno.aula.dto.CadastroAulaRequest;
import com.example.portalaluno.auth.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/aula")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Administrador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador')")
    @PostMapping
    public ResponseEntity<AulaResponse> cadastrarAula(@Valid @RequestBody AulaRequest request, @AuthenticationPrincipal User usuarioLogado) {
        AulaResponse aulaSalva = aulaService.cadastrarAula(request, usuarioLogado);
        return ResponseEntity.ok(aulaSalva);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication ,'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AulaResponse> atualizarAula(@Valid @PathVariable UUID id, @RequestBody CadastroAulaRequest request, @AuthenticationPrincipal User usuarioLogado) {
        AulaResponse aulaAtualizada = aulaService.atualizarAula(id, request.getAula(), usuarioLogado);
        return ResponseEntity.ok(aulaAtualizada);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication ,'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarAula(@PathVariable UUID id) {
        aulaService.cancelarAula(id);
        return ResponseEntity.ok("Aula cancelada com sucesso!");
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    @GetMapping("/aluno/{id}")
    public ResponseEntity<Page<AulaResponse>> listarAulasAluno(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        Page<AulaResponse> aulas = aulaService.listarAulasAlunos(id, pagina,tamanho);
        return ResponseEntity.ok(aulas);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    @GetMapping("/professor/{id}")
    public ResponseEntity<Page<AulaResponse>> listaAulasProfessor(
            @PathVariable UUID id,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int  tamanho) {

        Page<AulaResponse> aulas = aulaService.listarAulasProfessor(id, pagina,tamanho);
        return ResponseEntity.ok(aulas);
    }

    @GetMapping("/minhas-aulas")
    public ResponseEntity<Page<AulaResponse>> minhasAulas(
            @AuthenticationPrincipal User usuarioLogado,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        Page<AulaResponse> aulas = aulaService.minhaAulas(usuarioLogado, pagina, tamanho);
        return ResponseEntity.ok(aulas);
    }
}
