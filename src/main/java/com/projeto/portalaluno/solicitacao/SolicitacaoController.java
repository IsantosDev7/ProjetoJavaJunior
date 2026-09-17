package com.projeto.portalaluno.solicitacao;

import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoRequest;
import com.projeto.portalaluno.solicitacao.dto.SolicitacaoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitacao")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;


    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Secretário')")
    @PostMapping
    public ResponseEntity<SolicitacaoResponse> criarSolicitacao(
            @Valid @RequestBody SolicitacaoRequest request,
            @AuthenticationPrincipal User usuarioLogado) {
        SolicitacaoResponse reponse = solicitacaoService.criarSolicitacao(request, usuarioLogado);
        return ResponseEntity.ok(reponse);
    }
}
