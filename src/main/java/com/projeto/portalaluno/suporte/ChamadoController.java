package com.projeto.portalaluno.suporte;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.suporte.dto.ChamadoRequest;
import com.projeto.portalaluno.suporte.dto.ChamadoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.UUID;


@RestController
@RequestMapping("/suporte")
public class ChamadoController {

    @Autowired
    private ChamadoService chamadoService;

    @PostMapping("/criar")
    public ResponseEntity<ChamadoResponse> criarChamado(
            @Valid @RequestBody ChamadoRequest chamadoRequest,
            @AuthenticationPrincipal User usuarioLogado) {
        ChamadoResponse chamado =  chamadoService.criarChamado(chamadoRequest, usuarioLogado);
        return ResponseEntity.ok(chamado);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Secretário') or " +
            "hasRole('SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<ChamadoResponse>> listarChamados(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho,
            @RequestParam(required = false) LocalDateTime inicio,
            @RequestParam(required = false) LocalDateTime fim) {

        Page<ChamadoResponse> chamado = chamadoService.listarChamados(pagina, tamanho, inicio, fim);
        return ResponseEntity.ok(chamado);
    }

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Secretário') or " +
            "hasRole('SUPER_ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<Void> resolverChamado(@PathVariable UUID id){
        chamadoService.resolverChamado(id);
        return ResponseEntity.noContent().build();
    }
}
