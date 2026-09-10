package com.example.portalaluno.cronograma;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.aula.Aula;
import com.example.portalaluno.aula.AulaRepository;
import com.example.portalaluno.aula.dto.AulaResumoResponse;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.cronograma.dto.CronogramaResponse;
import com.example.portalaluno.cronograma.dto.DiaResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CronogramaService {

    private final AulaRepository aulaRepository;
    private final AlunoRepository alunoRepository;

    public CronogramaService(AulaRepository aulaRepository, AlunoRepository alunoRepository) {
        this.aulaRepository = aulaRepository;
        this.alunoRepository = alunoRepository;
    }

    @Transactional(readOnly = true)
    public CronogramaResponse obterMeuCronograma(LocalDate dataQualquer, User usuarioLogado) {
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não é um aluno"));

        return obterCronogramaPorAlunoId(dataQualquer, aluno.getId());
    }

    @Transactional(readOnly = true)
    public CronogramaResponse obterCronogramaDeAluno(LocalDate dataQualquer, UUID alunoId) {
        alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        return obterCronogramaPorAlunoId(dataQualquer, alunoId);
    }

    private CronogramaResponse obterCronogramaPorAlunoId(LocalDate dataQualquer, UUID alunoId) {
        LocalDate segundaFeira = calcularSegundaFeira(dataQualquer);
        LocalDate domingo = segundaFeira.plusDays(6);

        List<Aula> todasAulasDaSemana = aulaRepository.findByDataHoraAulaBetween(
                segundaFeira.atStartOfDay(),
                domingo.atTime(23, 59, 59)
        );

        List<Aula> aulasDoAluno = todasAulasDaSemana.stream()
                .filter(aula -> aula.getAluno().getId().equals(alunoId))
                .collect(Collectors.toList());

        CronogramaResponse cronograma = new CronogramaResponse();
        cronograma.setSemanaInicio(segundaFeira);
        cronograma.setSemanaFim(domingo);

        List<DiaResponse> dias = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate dataDia = segundaFeira.plusDays(i);
            DiaResponse dia = new DiaResponse();
            dia.setData(dataDia);
            dia.setNomeDia(obterNomeDia(dataDia));

            List<AulaResumoResponse> aulasDessaData = aulasDoAluno.stream()
                    .filter(aula -> aula.getDataHoraAula().toLocalDate().equals(dataDia))
                    .map(this::toAulaResumo)
                    .collect(Collectors.toList());

            dia.setAulas(aulasDessaData);
            dias.add(dia);
        }

        cronograma.setDias(dias);
        return cronograma;
    }

    private AulaResumoResponse toAulaResumo(Aula aula) {
        return new AulaResumoResponse(
                aula.getTitulo(),
                aula.getDataHoraAula().toLocalTime(),
                aula.getAluno().getName(),
                aula.getProfessor().getName(),
                aula.getModalidade()
        );
    }

    private String obterNomeDia(LocalDate data) {
        return data.getDayOfWeek().getDisplayName(
                java.time.format.TextStyle.FULL,
                java.util.Locale.forLanguageTag("pt-BR")
        );
    }

    private LocalDate calcularSegundaFeira(LocalDate data) {
        DayOfWeek diaDaSemana = data.getDayOfWeek();
        long diasParaAtras = diaDaSemana.getValue() - 1;
        return data.minusDays(diasParaAtras);
    }
}