package com.example.portalaluno.relatorio;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.relatorio.dto.RelatorioRequest;
import com.example.portalaluno.relatorio.dto.RelatorioResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorio")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @PreAuthorize("@funcionarioSecurity.temCargo(authentication ,'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<RelatorioResponse> criarRelatorio(@Valid @RequestBody RelatorioRequest request, @AuthenticationPrincipal User usuarioLogado) {
        RelatorioResponse relatorio = relatorioService.criarRelatorio(usuarioLogado,  request);
        return ResponseEntity.status(HttpStatus.CREATED).body(relatorio);
    }

}
