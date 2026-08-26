package com.example.portalaluno.aula;

import com.example.portalaluno.aula.dto.AulaResponse;
import com.example.portalaluno.aula.dto.CadastroAulaRequest;
import com.example.portalaluno.auth.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aula")
public class AulaController {

    @Autowired
    private AulaService aulaService;

    @PostMapping
    public AulaResponse cadastrarAula(@RequestBody CadastroAulaRequest request) {
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
}
