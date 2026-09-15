package com.projeto.portalaluno.auth.tokenconvite;

import com.projeto.portalaluno.auth.dto.TokenAceitarConviteRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/convite")
public class TokenConviteController {

    private final TokenConviteService tokenConviteService;

    public TokenConviteController(TokenConviteService tokenConviteService) {
        this.tokenConviteService = tokenConviteService;
    }

    @PostMapping("/aceitar")
    public ResponseEntity<Void> aceitarConvite(@Valid @RequestBody TokenAceitarConviteRequest request) {
        tokenConviteService.aceitarConvite(request.getToken(), request.getNovaSenha());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reenviar")
    public ResponseEntity<Void> reenviarConvite(@RequestParam String tokenExpirado) {
        tokenConviteService.reenviarConvitePorTokenAntigo(tokenExpirado);
        return ResponseEntity.noContent().build();
    }
}