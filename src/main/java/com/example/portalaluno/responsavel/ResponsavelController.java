package com.example.portalaluno.responsavel;


import com.example.portalaluno.auth.User;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import com.example.portalaluno.responsavel.dto.ResponsavelResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/responsavel")
public class ResponsavelController {

    @Autowired
    private ResponsavelService responsavelService;

    @GetMapping
    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or @funcionarioSecurity.temCargo(authentication, 'Professor') or @funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    public List<Responsavel> listarResponsavel(@RequestParam String name) {
        return responsavelService.consultarReponsavelPorNome(name);
    }
    @PutMapping("/{id}")
    @PreAuthorize("@funcionarioSecurity.temCargo(authentication, 'Coordenador') or @funcionarioSecurity.temCargo(authentication, 'Secretário') or hasRole('SUPER_ADMIN')")
    public ResponsavelResponse atualizarResponsavel(@PathVariable UUID id,@Valid @RequestBody ResponsavelRequest request) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Responsavel responsavelSalvo = responsavelService.atualizarResponsavel(request, usuarioLogado, id);

        return new ResponsavelResponse(
                responsavelSalvo.getName(),
                responsavelSalvo.getEmail(),
                responsavelSalvo.getPhone()
        );
    }

    @PutMapping("/meu/{id}")
    public ResponsavelResponse atualizarMeuResponsavel(@PathVariable UUID id, @Valid @RequestBody ResponsavelRequest request) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Responsavel responsavelAtualizado = responsavelService.atualizarMeuResponsavel(request, usuarioLogado, id);

        return new ResponsavelResponse(
                responsavelAtualizado.getName(),
                responsavelAtualizado.getEmail(),
                responsavelAtualizado.getPhone()
        );
    }
}
