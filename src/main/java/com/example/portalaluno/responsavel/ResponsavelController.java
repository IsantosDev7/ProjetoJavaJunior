package com.example.portalaluno.responsavel;


import com.example.portalaluno.auth.User;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import com.example.portalaluno.responsavel.dto.ResponsavelResponse;
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
@RequestMapping("/responsavel")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @GetMapping
    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Professor') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<ResponsavelResponse>> listarResponsavel(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "10") int tamanho) {

        return ResponseEntity.ok(responsavelService.listarResponsavel(name, tamanho, pagina));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or " +
            "@funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponsavelResponse> atualizarResponsavel(@PathVariable UUID id,@Valid @RequestBody ResponsavelRequest request) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResponsavelResponse responsavelSalvo = responsavelService.atualizarResponsavel(request, usuarioLogado, id);
        return ResponseEntity.ok(responsavelSalvo);
    }

    @PutMapping("/meu/{id}")
    public ResponseEntity<ResponsavelResponse> atualizarMeuResponsavel(@PathVariable UUID id, @Valid @RequestBody ResponsavelRequest request, @AuthenticationPrincipal  User usuarioLogado) {
        ResponsavelResponse responsavelAtualizado = responsavelService.atualizarMeuResponsavel(request, usuarioLogado, id);
        return ResponseEntity.ok(responsavelAtualizado);
    }
}
