package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoRequest;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.funcionario.Funcionario;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import com.example.portalaluno.responsavel.Responsavel;
import com.example.portalaluno.responsavel.ResponsavelRepository;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        // 1. Validação prévia de e-mail duplicado
        if (userRepository.findByEmail(dadosAluno.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um aluno cadastrado com este e-mail.");
        }
        if (dadosAluno.getPassword() == null || dadosAluno.getPassword().isBlank()) {
            throw new RuntimeException("Senha é obrigatória para cadastro de aluno.");
        }

        // 2. Criação e persistência do Usuário (Senha criptografada uma única vez)
        User usuario = new User();
        usuario.setEmail(dadosAluno.getEmail());
        usuario.setPassword(bCryptPasswordEncoder.encode(dadosAluno.getPassword()));
        usuario.setRole(UserRole.ALUNO);

        User usuarioSalvo = userRepository.save(usuario);

        // 3. Montagem do Aluno
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

        // 4. Verificação e associação do Responsável (se menor de idade)
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

    public Aluno atualizarCadastroAluno(UUID alunoId, AlunoRequest dadosAtualizados, User usuarioLogado) {
        Funcionario secretario = funcionarioRepository.findByUsuario(usuarioLogado)
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

    public List<Aluno> consultarAlunosPorNome(String name) {
        List<Aluno> alunos = alunoRepository.findByNameContainingIgnoreCase(name);

        if (alunos.isEmpty()) {
            throw new RuntimeException("Nenhum aluno encontrado com esse nome.");
        }
        return alunos;
    }
}