package com.example.portalaluno.relatorio;

import com.example.portalaluno.aula.Aula;
import com.example.portalaluno.aula.AulaRepository;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import com.example.portalaluno.relatorio.dto.RelatorioRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RelatorioService {

    private final AulaRepository aulaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final RelatorioRepository relatorioRepository;

    public RelatorioService(AulaRepository aulaRepository, RelatorioRepository relatorioRepository, FuncionarioRepository funcionarioRepository) {
        this.aulaRepository = aulaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.relatorioRepository = relatorioRepository;
    }

    @Transactional
    public Relatorio criarRelatorio(User usuarioLogado, RelatorioRequest dadosRelatorio) {
        Funcionario professor;

        if(dadosRelatorio.getProfessorId() != null){
            professor = funcionarioRepository.findById(dadosRelatorio.getProfessorId())
                    .orElseThrow(() -> new RuntimeException("Professor informado não encontrado"));
        }else {
            professor = funcionarioRepository.findByUsuario(usuarioLogado)
                    .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));
        }

        Aula aula = aulaRepository.findById(dadosRelatorio.getAulaid())
                .orElseThrow(() -> new RuntimeException("Aula não encontrada"));
        boolean existeRelatorio = relatorioRepository.existsByAulaId(aula.getId());
        if (existeRelatorio) {
            throw new RuntimeException("Já existe um relatório para esse aula");
        }

        Relatorio relatorio = new Relatorio();
        relatorio.setAula(aula);
        relatorio.setProfessor(professor);
        relatorio.setTexto(dadosRelatorio.getTexto());

        return relatorioRepository.save(relatorio);
    }
}
