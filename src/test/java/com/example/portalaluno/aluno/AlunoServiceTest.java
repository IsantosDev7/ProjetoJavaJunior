package com.example.portalaluno.aluno;

import com.example.portalaluno.aluno.dto.AlunoRequest;
import com.example.portalaluno.auth.User;
import com.example.portalaluno.auth.UserRepository;
import com.example.portalaluno.funcionario.FuncionarioRepository;
import com.example.portalaluno.responsavel.Responsavel;
import com.example.portalaluno.responsavel.ResponsavelRepository;
import com.example.portalaluno.responsavel.dto.ResponsavelRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private ResponsavelRepository responsavelRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private FuncionarioRepository funcionarioRepository;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    @DisplayName("Case 1: maior de idade cadastrado com sucesso!")
    void nãoDeveLancarNenhumaExcecaoPassandoOkPelaLogicaDeMaiorIdade() {

        AlunoRequest dadosAluno = new AlunoRequest();
        dadosAluno.setName("Israel");
        dadosAluno.setEmail("testeteste@gmail.com");
        dadosAluno.setPassword("123456");
        dadosAluno.setCpf("123456789");
        dadosAluno.setPhone("123456789");
        dadosAluno.setBirthDate(LocalDate.of(2004, 10, 10));
        dadosAluno.setAddress("rua do teste");
        dadosAluno.setCep("12345");
        dadosAluno.setState("N");
        dadosAluno.setCountry("Brasil");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User());
        when(alunoRepository.save(any())).thenReturn(new Aluno());

        assertDoesNotThrow(() -> alunoService.cadastrar(dadosAluno, null));
    }

    @Test
    @DisplayName("Case 2: menor de idade sem declarar responsavel, recusado")
    void deveLancarExcecaoQuandoMenorDeIdadeSemResponsavel() {

        AlunoRequest dadosAluno = new AlunoRequest();
        dadosAluno.setName("Israel");
        dadosAluno.setEmail("testeteste@gmail.com");
        dadosAluno.setPassword("123456");
        dadosAluno.setCpf("123456789");
        dadosAluno.setPhone("123456789");
        dadosAluno.setBirthDate(LocalDate.of(2015, 10, 10));
        dadosAluno.setAddress("rua do teste");
        dadosAluno.setCep("12345");
        dadosAluno.setState("N");
        dadosAluno.setCountry("Brasil");

        ResponsavelRequest dadosResponsavel = null;

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            alunoService.cadastrar(dadosAluno, dadosResponsavel);
        });
        assertEquals("Dados do responsável são obrigatórios para alunos menores de idade.", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 3: menor de idade declarando responsavel, aceito")
    void nãoDeveLancarNenhumaExcecaoPassandoOkPelaLogicaDeMenorIdade() {

        AlunoRequest dadosAluno = new AlunoRequest();
        dadosAluno.setName("Israel");
        dadosAluno.setEmail("testeteste@gmail.com");
        dadosAluno.setPassword("123456");
        dadosAluno.setCpf("123456789");
        dadosAluno.setPhone("123456789");
        dadosAluno.setBirthDate(LocalDate.of(2015, 10, 10));
        dadosAluno.setAddress("rua do teste");
        dadosAluno.setCep("12345");
        dadosAluno.setState("N");
        dadosAluno.setCountry("Brasil");

        ResponsavelRequest dadosResponsavel = new ResponsavelRequest();
        dadosResponsavel.setName("TestePai");
        dadosResponsavel.setCpf("123456789");
        dadosResponsavel.setPhone("123456789");
        dadosResponsavel.setEmail("testandopai@gmail.com");
        dadosResponsavel.setBirthdate(LocalDate.of(1981, 10, 10));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User());
        when(alunoRepository.save(any())).thenReturn(new Aluno());
        when(responsavelRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(responsavelRepository.save(any())).thenReturn(new Responsavel());

        assertDoesNotThrow(() -> alunoService.cadastrar(dadosAluno, dadosResponsavel));
    }

    @Test
    @DisplayName("Case 4: usuário já existe no banco de dados")
    void deveLancarExcecaoUsuarioJaExisteNoBancoDeDados() {
        AlunoRequest dadosAluno = new AlunoRequest();
        dadosAluno.setName("Israel");
        dadosAluno.setEmail("testeteste@gmail.com");
        dadosAluno.setPassword("123456");
        dadosAluno.setCpf("123456789");
        dadosAluno.setPhone("123456789");
        dadosAluno.setBirthDate(LocalDate.of(2004, 10, 10));
        dadosAluno.setAddress("rua do teste");
        dadosAluno.setCep("12345");
        dadosAluno.setState("N");
        dadosAluno.setCountry("Brasil");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            alunoService.cadastrar(dadosAluno, null);
        });

        assertEquals("Já existe um aluno cadastrado com este e-mail.", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 5: senha em branco")
    void deveLancarExcecaoSenhaEmBranco() {
        AlunoRequest dadosAluno = new AlunoRequest();
        dadosAluno.setName("Israel");
        dadosAluno.setEmail("testeteste@gmail.com");
        dadosAluno.setPassword(null);
        dadosAluno.setCpf("123456789");
        dadosAluno.setPhone("123456789");
        dadosAluno.setBirthDate(LocalDate.of(2004, 10, 10));
        dadosAluno.setAddress("rua do teste");
        dadosAluno.setCep("12345");
        dadosAluno.setState("N");
        dadosAluno.setCountry("Brasil");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            alunoService.cadastrar(dadosAluno, null);
        });
        assertEquals("Senha é obrigatória para cadastro de aluno.",  excecao.getMessage());
    }
}