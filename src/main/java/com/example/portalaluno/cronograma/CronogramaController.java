package com.example.portalaluno.cronograma;

import com.example.portalaluno.auth.User;
import com.example.portalaluno.cronograma.dto.CronogramaResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/cronograma")
public class CronogramaController {

    private final CronogramaService cronogramaService;

    public CronogramaController(CronogramaService cronogramaService) {
        this.cronogramaService = cronogramaService;
    }

    @GetMapping("/meu")
    public ResponseEntity<CronogramaResponse> meuCronograma(
            @RequestParam(name = "semana") LocalDate semana,
            @AuthenticationPrincipal User usuarioLogado) {

        CronogramaResponse cronograma = cronogramaService.obterMeuCronograma(semana, usuarioLogado);
        return ResponseEntity.ok(cronograma);
    }

    @GetMapping("/aluno/{alunoId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<CronogramaResponse> cronogramaDeAluno(
            @PathVariable UUID alunoId,
            @RequestParam(name = "semana") LocalDate semana) {

        CronogramaResponse cronograma = cronogramaService.obterCronogramaDeAluno(semana, alunoId);
        return ResponseEntity.ok(cronograma);
    }
}