package com.example.portalaluno.responsavel;


import com.example.portalaluno.auth.User;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import com.example.portalaluno.responsavel.dto.ResponsavelResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Responsavel> listarResponsavel(@RequestParam String name) {
        return responsavelService.consultarReponsavelPorNome(name);
    }
    @PutMapping
    public ResponsavelResponse atualizarResponsavel(@PathVariable UUID id, @RequestBody ResponsavelRequest request) {
        User usuarioLogado = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Responsavel responsavelSalvo = responsavelService.atualizarResponsavel(request, usuarioLogado, id);

        return new ResponsavelResponse(
                responsavelSalvo.getName(),
                responsavelSalvo.getEmail(),
                responsavelSalvo.getPhone()
        );
    }
}
