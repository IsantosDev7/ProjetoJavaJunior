package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoRequest;
import com.example.portalaluno.aluno.dto.AlunoResponse;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import com.example.portalaluno.responsavel.Responsavel;
import com.example.portalaluno.responsavel.ResponsavelRepository;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import com.example.portalaluno.responsavel.dto.ResponsavelResponse;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.portalaluno.aluno.AlunoStatusMatricula.CANCELADO;

@Service
public class AlunoService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final UserRepository userRepository;
    private final FuncionarioRepository funcionarioRepository;

    public AlunoService(AlunoRepository alunoRepository,
                        ResponsavelRepository responsavelRepository,
                        BCryptPasswordEncoder bCryptPasswordEncoder,
                        UserRepository userRepository, FuncionarioRepository funcionarioRepository) {
        this.alunoRepository = alunoRepository;
        this.responsavelRepository = responsavelRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    @Transactional
    public Aluno cadastrar(AlunoRequest dadosAluno, ResponsavelRequest dadosResponsavel) {

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

        // Criação do Aluno em cima do alunoRequest
        Aluno novoAluno = new Aluno();
        novoAluno.setUsuario(usuarioSalvo);
        novoAluno.setName(dadosAluno.getName());
        novoAluno.setCpf(dadosAluno.getCpf());
        novoAluno.setPhone(dadosAluno.getPhone());
        novoAluno.setBirthDate(dadosAluno.getBirthDate());
        novoAluno.setAddress(dadosAluno.getAddress());
        novoAluno.setCep(dadosAluno.getCep());
        novoAluno.setCity(dadosAluno.getCity());
        novoAluno.setState(dadosAluno.getState());
        novoAluno.setCountry(dadosAluno.getCountry());

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

        return alunoRepository.save(novoAluno);
    }

    // rota de atualização pelo próprio usuário
    public Aluno atualizarMeuCadastro(AlunoRequest dadosAtualizados, User usuarioLogado) {
        Aluno aluno = alunoRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado para este usuário"));

        aluno.setName(dadosAtualizados.getName());
        aluno.setCpf(dadosAtualizados.getCpf());
        aluno.setPhone(dadosAtualizados.getPhone());
        aluno.setBirthDate(dadosAtualizados.getBirthDate());
        aluno.setAddress(dadosAtualizados.getAddress());
        aluno.setCep(dadosAtualizados.getCep());
        aluno.setCity(dadosAtualizados.getCity());
        aluno.setState(dadosAtualizados.getState());
        aluno.setCountry(dadosAtualizados.getCountry());

        return alunoRepository.save(aluno);
    }

    // rota de atualização pelo funcionário
    public Aluno atualizarCadastroAluno(UUID alunoId, AlunoRequest dadosAtualizados, User usuarioLogado) {
        Funcionario funcionario = funcionarioRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Usuário logado não é um funcionário"));
        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RuntimeException("Aluno inexistente com esse id"));

        aluno.setName(dadosAtualizados.getName());
        aluno.setCpf(dadosAtualizados.getCpf());
        aluno.setPhone(dadosAtualizados.getPhone());
        aluno.setBirthDate(dadosAtualizados.getBirthDate());
        aluno.setAddress(dadosAtualizados.getAddress());
        aluno.setCep(dadosAtualizados.getCep());
        aluno.setCity(dadosAtualizados.getCity());
        aluno.setState(dadosAtualizados.getState());
        aluno.setCountry(dadosAtualizados.getCountry());

        return alunoRepository.save(aluno);
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
    public Aluno cancelarMatriculaAluno(UUID idAluno) {

        Aluno aluno = alunoRepository.findById(idAluno)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado."));
        if (aluno.getAlunoStatusMatricula() == CANCELADO) {
            throw new RuntimeException("A matrícula desse aluno já foi cancelada.");
        }

        aluno.setAlunoStatusMatricula(AlunoStatusMatricula.CANCELADO);
        return alunoRepository.save(aluno);
    }
}