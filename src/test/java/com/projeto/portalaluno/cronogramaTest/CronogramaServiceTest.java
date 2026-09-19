package com.projeto.portalaluno.cronogramaTest;

import com.projeto.portalaluno.aluno.Aluno;
import com.projeto.portalaluno.aluno.AlunoRepository;
import com.projeto.portalaluno.aula.Aula;
import com.projeto.portalaluno.aula.AulaRepository;
import com.projeto.portalaluno.aula.roles.Modalidade;
import com.projeto.portalaluno.auth.User;
import com.projeto.portalaluno.cronograma.CronogramaService;
import com.projeto.portalaluno.cronograma.dto.CronogramaResponse;
import com.projeto.portalaluno.cronograma.dto.DiaResponse;
import com.projeto.portalaluno.funcionario.Funcionario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CronogramaServiceTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private CronogramaService cronogramaService;

    private User usuario;
    private Aluno aluno;
    private Aula aula;

    @BeforeEach
    void setup() {
        usuario = new User();
        usuario.setId(UUID.randomUUID());

        aluno = new Aluno();
        aluno.setId(UUID.randomUUID());
        aluno.setName("Aluno Teste");

        Funcionario professor = new Funcionario();
        professor.setId(UUID.randomUUID());
        professor.setName("Professor Teste");

        aula = new Aula();
        aula.setId(UUID.randomUUID());
        aula.setTitulo("Aula de Matemática");
        aula.setModalidade(Modalidade.PRESENCIAL);
        aula.setDuracaoAula(60);
        aula.setAluno(aluno);
        aula.setProfessor(professor);
        aula.setDataHoraAula(LocalDateTime.now().withHour(10).withMinute(0));
    }

    @Test
    @DisplayName("Case 1: Get my schedule successfully")
    void deveObterMeuCronogramaComSucesso() {
        LocalDate data = LocalDate.now();

        when(alunoRepository.findByUsuario(usuario)).thenReturn(Optional.of(aluno));
        when(aulaRepository.findByDataHoraAulaBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aula));

        CronogramaResponse resultado = cronogramaService.obterMeuCronograma(data, usuario);

        assertNotNull(resultado);
        assertNotNull(resultado.getSemanaInicio());
        assertNotNull(resultado.getSemanaFim());
        assertEquals(7, resultado.getDias().size());
        verify(alunoRepository).findByUsuario(usuario);
        verify(aulaRepository).findByDataHoraAulaBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Case 2: Get my schedule fails - user is not student")
    void deveLancarExcecaoUsuarioNaoEhAluno() {
        LocalDate data = LocalDate.now();

        when(alunoRepository.findByUsuario(usuario)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            cronogramaService.obterMeuCronograma(data, usuario);
        });

        assertEquals("Usuário não é um aluno", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 3: Get student schedule successfully")
    void deveObterCronogramaDeAlunoComSucesso() {
        LocalDate data = LocalDate.now();
        UUID alunoId = aluno.getId();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.of(aluno));
        when(aulaRepository.findByDataHoraAulaBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aula));

        CronogramaResponse resultado = cronogramaService.obterCronogramaDeAluno(data, alunoId);

        assertNotNull(resultado);
        assertEquals(7, resultado.getDias().size());
        verify(alunoRepository).findById(alunoId);
    }

    @Test
    @DisplayName("Case 4: Get student schedule fails - student not found")
    void deveLancarExcecaoAlunoNaoEncontrado() {
        LocalDate data = LocalDate.now();
        UUID alunoId = UUID.randomUUID();

        when(alunoRepository.findById(alunoId)).thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(RuntimeException.class, () -> {
            cronogramaService.obterCronogramaDeAluno(data, alunoId);
        });

        assertEquals("Aluno não encontrado", excecao.getMessage());
    }

    @Test
    @DisplayName("Case 5: Get schedule with multiple classes on same day")
    void deveObterCronogramaComMultiplasAulasNoMesmoDia() {
        LocalDate data = LocalDate.now();

        Aula aula2 = new Aula();
        aula2.setTitulo("Aula de Português");
        aula2.setDataHoraAula(LocalDateTime.now().withHour(14).withMinute(0));
        aula2.setAluno(aluno);
        aula2.setProfessor(new Funcionario());

        when(alunoRepository.findByUsuario(usuario)).thenReturn(Optional.of(aluno));
        when(aulaRepository.findByDataHoraAulaBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(aula, aula2));

        CronogramaResponse resultado = cronogramaService.obterMeuCronograma(data, usuario);

        assertNotNull(resultado);
        assertEquals(7, resultado.getDias().size());
    }

    @Test
    @DisplayName("Case 6: Get schedule with correct week boundaries")
    void deveVerificarLimitesDaSemana() {
        LocalDate data = LocalDate.now();

        when(alunoRepository.findByUsuario(usuario)).thenReturn(Optional.of(aluno));
        when(aulaRepository.findByDataHoraAulaBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        CronogramaResponse resultado = cronogramaService.obterMeuCronograma(data, usuario);

        assertNotNull(resultado);
        assertNotNull(resultado.getSemanaInicio());
        assertNotNull(resultado.getSemanaFim());
        // A semana deve ter 7 dias
        assertEquals(7, resultado.getDias().size());
    }
}
