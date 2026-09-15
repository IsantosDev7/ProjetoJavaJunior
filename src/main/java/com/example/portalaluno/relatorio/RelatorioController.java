package com.example.portalaluno.relatorio;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.relatorio.dto.RelatorioRequest;
import com.example.portalaluno.relatorio.dto.RelatorioResponse;
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
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;
    @Autowired
    private RelatorioSecurity relatorioSecurity;

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication ,'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Administrador')")
    @PostMapping
    public ResponseEntity<RelatorioResponse> criarRelatorio(@Valid @RequestBody RelatorioRequest request, @AuthenticationPrincipal User usuarioLogado) {
        RelatorioResponse relatorio = relatorioService.criarRelatorio(usuarioLogado,  request);
        return ResponseEntity.status(HttpStatus.CREATED).body(relatorio);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication ,'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "hasAnyRole('SUPER_ADMIN', 'ALUNO')")
    @GetMapping("/meus")
    public ResponseEntity<Page<RelatorioResponse>> meusRelatorios(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String titulo,
            @AuthenticationPrincipal User usuarioLogado) {

        Page<RelatorioResponse> relatorios = relatorioService.meusRelatorios(usuarioLogado, pagina, tamanho, titulo);

        return ResponseEntity.ok(relatorios);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "hasRole('SUPER_ADMIN')")
    @GetMapping("/todos")
    public ResponseEntity<Page<RelatorioResponse>> listaRelatorios(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) String nameProfessor,
            @RequestParam(required = false) String nameAluno){

        Page<RelatorioResponse> relatorios = relatorioService.listaRelatorios(pagina, tamanho, nameProfessor, nameAluno);
        return ResponseEntity.ok(relatorios);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("@relatorioSecurity.podeEditar(authentication, #id)")
    public ResponseEntity<Void> cancelarRelatorio(@PathVariable UUID id) {
        relatorioService.cancelarRelatorio(id);
        return  ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("@relatorioSecurity.podeEditar(authentication, #id)")
    public ResponseEntity<RelatorioResponse> atualizarTextoRelatorio(@PathVariable UUID id, @Valid @RequestBody RelatorioRequest request, @AuthenticationPrincipal User usuarioLogado) {
        RelatorioResponse relatorio = relatorioService.atualizarTextoRelatorio(usuarioLogado, request, id);
        return ResponseEntity.ok(relatorio);
    }

    @PutMapping("/confirmar-leitura/{id}")
    public ResponseEntity<Void> confirmarLeitura(@PathVariable UUID id, @AuthenticationPrincipal User usuarioLogado) {
        relatorioService.confirmarLeitura(id, usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}
