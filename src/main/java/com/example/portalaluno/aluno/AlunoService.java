package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoRequest;
import com.example.portalaluno.aluno.dto.AlunoResponse;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.responsavel.Responsavel;
import com.example.portalaluno.responsavel.ResponsavelRepository;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import com.example.portalaluno.responsavel.dto.ResponsavelResponse;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;
import static com.example.portalaluno.aluno.AlunoStatusMatricula.CANCELADO;

@Service
public class AlunoService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final UserRepository userRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        ResponsavelRepository responsavelRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder,
                        UserRepository userRepository) {
        this.alunoRepository = alunoRepository;
        this.responsavelRepository = responsavelRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    private AlunoResponse toResponse(Aluno aluno) {
        ResponsavelResponse responsavelResumo = null;
        if (aluno.getResponsavel() != null) {
            responsavelResumo = new ResponsavelResponse(
                    aluno.getResponsavel().getName(),
                    aluno.getResponsavel().getPhone(),
                    aluno.getResponsavel().getEmail()
            );
        }
        return new AlunoResponse(
                aluno.getId(),
                aluno.getName(),
                aluno.getUsuario().getEmail(),
                aluno.getCpf(),
                aluno.getPhone(),
                aluno.getBirthDate(),
                responsavelResumo
        );
    }

    private void atualizarDadosAluno(Aluno aluno, AlunoRequest dados) {
        aluno.setName(dados.getName());
        aluno.setCpf(dados.getCpf());
        aluno.setPhone(dados.getPhone());
        aluno.setBirthDate(dados.getBirthDate());
        aluno.setAddress(dados.getAddress());
        aluno.setCep(dados.getCep());
        aluno.setCity(dados.getCity());
        aluno.setState(dados.getState());
        aluno.setCountry(dados.getCountry());
    }

    @Transactional
    public AlunoResponse cadastrar(AlunoRequest dadosAluno, ResponsavelRequest dadosResponsavel) {

        // Validação prévia de e-mail duplicado
        if (userRepository.findByEmail(dadosAluno.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um aluno cadastrado com este e-mail.");
        } // Validação de senha preenchida
        if (dadosAluno.getPassword() == null || dadosAluno.getPassword().isBlank()) {
            throw new RuntimeException("Senha é obrigatória para cadastro de aluno.");
        }

        // Criação e persistência do Usuário (Senha criptografada uma única vez)
        User usuario = new User();
        usuario.setEmail(dadosAluno.getEmail());
        usuario.setRole(UserRole.ALUNO);
        usuario.setEnabled(false);
        usuario.setPassword(bCryptPasswordEncoder.encode(dadosAluno.getPassword()));
        User usuarioSalvo = userRepository.save(usuario);

        Aluno novoAluno = new Aluno();
        novoAluno.setUsuario(usuarioSalvo);
        atualizarDadosAluno(novoAluno, dadosAluno);

        // Verificação e associação do Responsável (se menor de idade)
        if (novoAluno.isMinor()) {
            if (dadosResponsavel == null) {
                throw new RuntimeException("Dados do responsável são obrigatórios para alunos menores de idade.");
            }

            Optional<Responsavel> responsavelExistente = responsavelRepository.findByCpf(dadosResponsavel.getCpf());

            Responsavel responsavel;
            if (responsavelExistente.isPresent()) {
                responsavel = responsavelExistente.get();
            } else {
                Responsavel novoResponsavel = new Responsavel();
                novoResponsavel.setName(dadosResponsavel.getName());
                novoResponsavel.setCpf(dadosResponsavel.getCpf());
                novoResponsavel.setEmail(dadosResponsavel.getEmail());
                novoResponsavel.setPhone(dadosResponsavel.getPhone());
                novoResponsavel.setBirthdate(dadosResponsavel.getBirthdate());

                responsavel = responsavelRepository.save(novoResponsavel);
            }

            novoAluno.setResponsavel(responsavel);
        }
        Aluno alunoSalvo = alunoRepository.save(novoAluno);
        return toResponse(alunoSalvo);
    }

    // rota de atualização pelo próprio usuário
    @Transactional
    public AlunoResponse atualizarMeuCadastro(AlunoRequest dadosAtualizados, User usuarioLogado) {
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado para este usuário"));

        atualizarDadosAluno(aluno, dadosAtualizados);
        Aluno alunoSalvo = alunoRepository.save(aluno);
        return toResponse(alunoSalvo);
    }

    // rota de atualização pelo funcionário
    @Transactional
    public AlunoResponse atualizarCadastroAluno(UUID alunoId, AlunoRequest dadosAtualizados) {
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno inexistente com esse id"));

        atualizarDadosAluno(aluno, dadosAtualizados);
        Aluno alunoSalvo = alunoRepository.save(aluno);
        return toResponse(alunoSalvo);
    }

    //consultar aluno com possibilidade de filtrar por nome, se for maior de idade response só retorna dados aluno, se menor, dados aluno + dados do responsável
    public Page<AlunoResponse> consultarAlunos(String name, int pagina, int tamanho) {
        Sort.Order order = Sort.Order.asc("name").ignoreCase();
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(order));

        Page<Aluno> page;
        if (name != null && !name.isBlank()) {
            page = alunoRepository.findByNameContainingIgnoreCase(name, pageable);
        } else {
            page = alunoRepository.findAll(pageable);
        }

        return page.map(this::toResponse);
    }

    // lógica de aprovaçao cadastro de alunos
    @Transactional
    public void aprovarAluno(UUID idAluno) {
        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));

        if (aluno.getStatus() == AlunoStatusCadastro.APROVADO) {
            throw new RuntimeException("Este aluno já está aprovado.");
        }

        aluno.setStatus(AlunoStatusCadastro.APROVADO);

        aluno.getUsuario().setEnabled(true);

        alunoRepository.save(aluno);
    }

    // lógica para deletar aluno
    @Transactional
    public void cancelarMatriculaAluno(UUID idAluno) {

        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        if (aluno.getAlunoStatusMatricula() == CANCELADO) {
            throw new RuntimeException("A matrícula desse aluno já foi cancelada.");
        }
        aluno.setAlunoStatusMatricula(AlunoStatusMatricula.CANCELADO);
        alunoRepository.save(aluno);
    }
}