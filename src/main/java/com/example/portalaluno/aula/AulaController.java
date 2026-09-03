package com.example.portalaluno.aula;

import com.example.portalaluno.aula.dto.AulaResponse;
import com.example.portalaluno.aula.dto.CadastroAulaRequest;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.FuncionarioSecurity;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/aula")
public class AulaController {

    @Autowired
    private AulaService aulaService;
    @Autowired
    private FuncionarioSecurity funcionarioSecurity;

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication ,'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "hasRole('SUPER_ADMIN')")
    @PostMapping
    public AulaResponse cadastrarAula(@Valid @RequestBody CadastroAulaRequest request) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Aula aulaSalva = aulaService.cadastrarAula(request.getAula(), usuarioLogado);

        return new AulaResponse(
        aulaSalva.getId(),
        aulaSalva.getTitulo(),
        aulaSalva.getModalidade(),
        aulaSalva.getDuracaoAula(),
        aulaSalva.getAluno().getId(),
        aulaSalva.getDataHoraAula()
        );
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication ,'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "hasRole('SUPER_ADMIN')")
    @PutMapping("/{id}")
    public AulaResponse atualizarAula(@Valid @PathVariable UUID id, @RequestBody CadastroAulaRequest request) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Aula aulaAtualizada = aulaService.atualizarAula(id, request.getAula(), usuarioLogado);

        return new AulaResponse(
                aulaAtualizada.getId(),
                aulaAtualizada.getTitulo(),
                aulaAtualizada.getModalidade(),
                aulaAtualizada.getDuracaoAula(),
                aulaAtualizada.getAluno().getId(),
                aulaAtualizada.getDataHoraAula()
        );
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
