package com.example.portalaluno.suporte;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.suporte.dto.ChamadoRequest;
import com.example.portalaluno.suporte.dto.ChamadoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suporte")
public class ChamadoController {

    @Autowired
    private ChamadoService chamadoService;

    @PostMapping("/criar")
    public ResponseEntity<ChamadoResponse> criarChamado(@Valid @RequestBody ChamadoRequest chamadoRequest, @AuthenticationPrincipal User usuarioLogado) {
        ChamadoResponse chamado =  chamadoService.criarChamado(chamadoRequest, usuarioLogado);
        return ResponseEntity.ok(chamado);
    }
}
