package com.example.portalaluno.relatorio;

import com.example.portalaluno.aluno.Aluno;
import com.example.portalaluno.aluno.AlunoRepository;
import com.example.portalaluno.aula.Aula;
import com.example.portalaluno.aula.AulaRepository;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import com.example.portalaluno.relatorio.dto.RelatorioRequest;
import com.example.portalaluno.relatorio.dto.RelatorioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class RelatorioService {

    private final AulaRepository aulaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final RelatorioRepository relatorioRepository;
    private final AlunoRepository alunoRepository;

    public RelatorioService(AulaRepository aulaRepository, RelatorioRepository relatorioRepository, FuncionarioRepository funcionarioRepository, AlunoRepository alunoRepository) {
        this.aulaRepository = aulaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.relatorioRepository = relatorioRepository;
        this.alunoRepository = alunoRepository;
    }

    private RelatorioResponse toResponse(Relatorio relatorio) {
        return new RelatorioResponse(
                relatorio.getAula().getId(),
                relatorio.getTexto(),
                relatorio.getProfessor().getId(),
                relatorio.getLido(),
                relatorio.getCreatedAt()
        );
    }

    @Transactional
    public RelatorioResponse criarRelatorio(User usuarioLogado, RelatorioRequest dadosRelatorio) {
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

        Relatorio relatorioSalvo = relatorioRepository.save(relatorio);
        return toResponse(relatorioSalvo);
    }

    @Transactional(readOnly = true)
    public Page<RelatorioResponse> meusRelatorios(User usuarioLogado, int pagina, int tamanho, String titulo) {
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Direction.DESC, "aula.dataHoraAula")
        );

        boolean temFiltroTitulo = titulo != null && !titulo.isBlank();

        Optional<Funcionario> professorOpt = funcionarioRepository.findByUsuario(usuarioLogado);

        if (professorOpt.isPresent()) {
            UUID professorId = professorOpt.get().getId();
            Page<Relatorio> page = temFiltroTitulo
                    ? relatorioRepository.findByProfessorIdAndAulaTituloContainingIgnoreCase(professorId, titulo, pageable)
                    : relatorioRepository.findByProfessorId(professorId, pageable);

            return page.map(this::toResponse);
        }

        Optional<Aluno> alunoOpt = alunoRepository.findByUsuario(usuarioLogado);

        if (alunoOpt.isPresent()) {
            UUID alunoId = alunoOpt.get().getId();
            Page<Relatorio> page = temFiltroTitulo
                    ? relatorioRepository.findByAulaAlunoIdAndAulaTituloContainingIgnoreCase(alunoId, titulo, pageable)
                    : relatorioRepository.findByAulaAlunoId(alunoId, pageable);

            return page.map(this::toResponse);
        }

        throw new RuntimeException("Perfil do usuário logado não encontrado");
    }

    @Transactional(readOnly = true)
    public Page<RelatorioResponse> listaRelatorios(int pagina, int tamanho, String nameProfessor, String nomeAluno) {
        Pageable pageable = PageRequest.of(
                pagina,
                tamanho,
                Sort.by(Sort.Direction.DESC, "aula.dataHoraAula")
        );

        Page<Relatorio> page;
        if (nameProfessor != null && !nameProfessor.isBlank()) {
            page = relatorioRepository.findByProfessorNameContainingIgnoreCase(nameProfessor, pageable);
        } else if  (nomeAluno != null && !nomeAluno.isBlank()) {
            page = relatorioRepository.findByAulaAlunoNameContainingIgnoreCase(nomeAluno, pageable);
        } else {
            page = relatorioRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional
    public RelatorioResponse cancelarRelatorio(UUID relatorioId, User usuarioLogado) {
        Relatorio relatorio = relatorioRepository.findById(relatorioId)
                .orElseThrow(() -> new RuntimeException("Relatório não encontrado"));
        if (relatorio.getStatus().equals(StatusRelatorio.CANCELADO)) {
            throw new RuntimeException("Relatório em questão já se encontra cancelado");
        }

        boolean seDono = relatorio.getProfessor().getUsuario().getId().equals(usuarioLogado.getId());
        boolean seAdmin = usuarioLogado.getRole() == UserRole.SUPER_ADMIN;
        boolean seCoordenador = funcionarioRepository.findByUsuario(usuarioLogado)
                .map(func -> func.getCargos().stream()
                        .anyMatch(cargo -> cargo.getName().equalsIgnoreCase("Coordenador")))
                .orElse(false);


        if (!seDono && !seAdmin && !seCoordenador) {
            throw new RuntimeException("Acesso negado: você não tem permissão para cancelar este relatório");
        }

        relatorio.setStatus(StatusRelatorio.CANCELADO);
        return toResponse(relatorioRepository.save(relatorio));
    }
}
