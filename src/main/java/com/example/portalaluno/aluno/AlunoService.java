package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoRequest;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.auth.UserRole;
import com.example.portalaluno.responsavel.Responsavel;
import com.example.portalaluno.responsavel.ResponsavelRepository;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlunoService {

    private final BCryptPasswordEncoder  bCryptPasswordEncoder;
    private final AlunoRepository alunoRepository;
    private final ResponsavelRepository responsavelRepository;
    private final UserRepository userRepository;

    public AlunoService(AlunoRepository alunoRepository, ResponsavelRepository responsavelRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserRepository userRepository) {
        this.alunoRepository = alunoRepository;
        this.responsavelRepository = responsavelRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
    }

    public Aluno cadastrar(AlunoRequest dadosAluno, ResponsavelRequest dadosResponsavel) {

        User usuario = new User();
        usuario.setEmail(dadosAluno.getEmail());
        usuario.setPassword(bCryptPasswordEncoder.encode(dadosAluno.getPassword()));
        usuario.setRole(UserRole.ALUNO);

        Aluno novoAluno = new Aluno();
        novoAluno.setUsuario(usuario);
        novoAluno.setName(dadosAluno.getName());
        novoAluno.setCpf(dadosAluno.getCpf());
        novoAluno.setPhone(dadosAluno.getPhone());
        novoAluno.setBirthDate(dadosAluno.getBirthDate());
        novoAluno.setAddress(dadosAluno.getAddress());
        novoAluno.setCep(dadosAluno.getCep());
        novoAluno.setCity(dadosAluno.getCity());
        novoAluno.setState(dadosAluno.getState());
        novoAluno.setCountry(dadosAluno.getCountry());


        if (userRepository.findByEmail(dadosAluno.getEmail()).isPresent()) {
            throw new RuntimeException("Já existe um aluno cadastrado com este e-mail.");
        }

        boolean menorDeIdade = novoAluno.isMinor();

        if (menorDeIdade) {
            // Busca se já existe um responsável com esse CPF
            Optional<Responsavel> responsavelExistente = responsavelRepository.findByCpf(dadosResponsavel.getCpf());

            Responsavel responsavel;
            if (responsavelExistente.isPresent()) {
                // Já existe: reaproveita o cadastro
                responsavel = responsavelExistente.get();
            } else {
                // Não existe: cria um novo responsável no banco
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

        if (novoAluno.getUsuario() != null && novoAluno.getUsuario().getPassword() != null) {
            // Pega a senha em texto puro, criptografa e joga de volta no usuário
            String senhaCriptografada = bCryptPasswordEncoder.encode(novoAluno.getUsuario().getPassword());
            novoAluno.getUsuario().setPassword(senhaCriptografada);
        }

        return alunoRepository.save(novoAluno);
    }

    public Aluno atualizar(Aluno novoAluno) {
        return alunoRepository.save(novoAluno);
    }

    public List<Aluno> consultarAlunosPorNome(String name) {
        List<Aluno> alunos = alunoRepository.findByNameContainingIgnoreCase(name);

        if (alunos.isEmpty()) {
            throw new RuntimeException("Nenhum aluno encontrado com esse nome.");
        }
        return alunos;
    }

}

